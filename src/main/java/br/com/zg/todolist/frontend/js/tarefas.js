import { mostrarMensagem } from "./ui.js";
import {
    prioridadeValida,
    dataFormatoValido,
    dataNaoAnteriorHoje
} from "../regex/validacao.js";

const listaTarefas = document.getElementById("listaTarefas");
const formTarefa = document.getElementById("formTarefa");
const botaoFormulario = document.getElementById("botaoFormulario");
const botaoCancelarEdicao = document.getElementById("botaoCancelarEdicao");
const selecionarTodas = document.getElementById("selecionarTodas");
const statusMultiplo = document.getElementById("statusMultiplo");
const botaoStatusMultiplo = document.getElementById("botaoStatusMultiplo");
const buscaTarefas = document.getElementById("buscaTarefas");
const acoesMultiplas = document.getElementById("acoesMultiplas");
const quantidadeSelecionadas = document.getElementById("quantidadeSelecionadas");
const modalConfirmacao = document.getElementById("modalConfirmacao");
const mensagemModalConfirmacao = document.getElementById("mensagemModalConfirmacao");
const botaoCancelarRemocao = document.getElementById("botaoCancelarRemocao");
const botaoConfirmarRemocao = document.getElementById("botaoConfirmarRemocao");

let tarefas = carregarTarefas();
let tarefaEmEdicao = null;
let statusDashboard = null;
let tarefasSelecionadas = new Set();
let tarefaPendenteRemocao = null;

window.addEventListener("tarefas:filtrar-status", (evento) => {
    statusDashboard = evento.detail.status;
    limparSelecao();
    atualizarSelecaoDashboard();
    listarTarefas();
});

window.addEventListener("tarefas:mostrar-todas", () => {
    statusDashboard = null;
    limparSelecao();
    atualizarSelecaoDashboard();
    listarTarefas();
});

window.addEventListener("storage", (evento) => {
    if (evento.key !== "tarefas") return;
    tarefas = evento.newValue ? JSON.parse(evento.newValue) : [];
    limparSelecao();
    listarTarefas();
});

function carregarTarefas() {
    const dados = localStorage.getItem("tarefas");
    return dados ? JSON.parse(dados) : [];
}

export function salvarTarefas() {
    localStorage.setItem("tarefas", JSON.stringify(tarefas));
}

export function formatarData(data) {
    if (!data) return "";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}

function criarCardTarefa(tarefa, incluirAcoes = true) {
    const card = document.createElement("div");
    card.classList.add("tarefa", `status-${tarefa.status}`);
    card.dataset.tarefaId = tarefa.id;

    const infoAlarme = tarefa.alarme
        ? `<p><strong>Alarme:</strong> ${tarefa.alarme}</p>`
        : `<p><strong>Alarme:</strong> Não configurado</p>`;

    const infoAcoes = incluirAcoes
        ? `<div class="acoes-tarefa">
                <button type="button" class="btn-remover" data-id="${tarefa.id}" aria-label="Excluir ${tarefa.nome}">Excluir</button>
                <button type="button" class="btn-editar" data-id="${tarefa.id}" aria-label="Editar ${tarefa.nome}">Editar</button>
           </div>`
        : "";

    card.innerHTML = `
        <div class="titulo-tarefa">
            <input type="checkbox" class="checkbox-tarefa" value="${tarefa.id}" aria-label="Selecionar tarefa ${tarefa.nome}" ${tarefasSelecionadas.has(tarefa.id) ? "checked" : ""}>
            <h3>${tarefa.nome}</h3>
            <span class="status-indicador">${descricaoStatus(tarefa.status)}</span>
        </div>
        <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
        <p><strong>Prazo:</strong> <span class="prazo-indicador ${classePrazo(tarefa)}">${descricaoPrazo(tarefa)}</span> · ${formatarData(tarefa.dataTermino)}</p>
        <p><strong>Prioridade:</strong> ${tarefa.prioridade}/5</p>
        <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
        ${infoAlarme}
        ${infoAcoes}
    `;

    return card;
}

export function listarTarefas() {
    listaTarefas.innerHTML = "";
    atualizarResumoDashboard();

    const tarefasDoStatus = statusDashboard
        ? tarefas.filter((tarefa) => tarefa.status === statusDashboard)
        : tarefas;
    const busca = buscaTarefas.value.trim().toLocaleLowerCase("pt-BR");
    const tarefasVisiveis = tarefasDoStatus.filter((tarefa) => {
        const texto = `${tarefa.nome} ${tarefa.descricao} ${tarefa.categoria}`.toLocaleLowerCase("pt-BR");
        return texto.includes(busca);
    });
    const tituloLista = document.getElementById("tituloListaTarefas");

    if (tituloLista) {
        tituloLista.textContent = statusDashboard
            ? `Tarefas: ${descricaoStatus(statusDashboard)}`
            : "Minhas Tarefas";
    }

    tarefasVisiveis.forEach((tarefa) => {
        listaTarefas.appendChild(criarCardTarefa(tarefa));
    });

    if (tarefasVisiveis.length === 0) {
        mostrarEstadoVazio(tarefasDoStatus.length, busca);
    }

    atualizarAcoesMultiplas(tarefasVisiveis);
}

function descricaoStatus(status) {
    const descricoes = {
        TODO: "A fazer",
        DOING: "Em andamento",
        DONE: "Concluídas"
    };

    return descricoes[status] || "Minhas Tarefas";
}

function descricaoPrazo(tarefa) {
    if (tarefa.status === "DONE") return "Concluída";
    const diasRestantes = calcularDiasAtePrazo(tarefa.dataTermino);
    if (diasRestantes < 0) return `Atrasada há ${Math.abs(diasRestantes)} dia(s)`;
    if (diasRestantes === 0) return "Vence hoje";
    if (diasRestantes === 1) return "Vence amanhã";
    return `Em ${diasRestantes} dias`;
}

function classePrazo(tarefa) {
    if (tarefa.status === "DONE") return "prazo-concluido";
    const diasRestantes = calcularDiasAtePrazo(tarefa.dataTermino);
    if (diasRestantes < 0) return "prazo-atrasado";
    if (diasRestantes <= 1) return "prazo-proximo";
    return "prazo-normal";
}

function calcularDiasAtePrazo(data) {
    const [ano, mes, dia] = data.split("-").map(Number);
    const prazo = new Date(ano, mes - 1, dia);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return Math.round((prazo - hoje) / 86400000);
}

function mostrarEstadoVazio(totalNoStatus, busca) {
    const estado = document.createElement("div");
    estado.className = "lista-vazia";
    const texto = document.createElement("p");
    const acao = document.createElement("button");
    acao.type = "button";

    if (totalNoStatus === 0 && tarefas.length === 0) {
        texto.textContent = "Sua lista ainda está vazia. Crie uma tarefa para começar.";
        acao.textContent = "Cadastrar tarefa";
        acao.addEventListener("click", () => {
            document.querySelector('.menu-item[data-secao="cadastrar"]')?.click();
        });
    } else if (busca) {
        texto.textContent = "Nenhuma tarefa corresponde à sua busca.";
        acao.textContent = "Limpar busca";
        acao.addEventListener("click", () => {
            buscaTarefas.value = "";
            limparSelecao();
            listarTarefas();
            buscaTarefas.focus();
        });
    } else if (statusDashboard) {
        texto.textContent = `Não há tarefas com status ${descricaoStatus(statusDashboard)}.`;
        acao.textContent = "Ver todas as tarefas";
        acao.addEventListener("click", () => {
            document.querySelector('.menu-item[data-secao="tarefas"]')?.click();
        });
    } else {
        texto.textContent = "Nenhuma tarefa encontrada.";
        acao.textContent = "Cadastrar tarefa";
        acao.addEventListener("click", () => {
            document.querySelector('.menu-item[data-secao="cadastrar"]')?.click();
        });
    }

    estado.append(texto, acao);
    listaTarefas.appendChild(estado);
}

function atualizarSelecaoDashboard() {
    document.querySelectorAll(".stat-card[data-status]").forEach((card) => {
        card.setAttribute("aria-pressed", String(card.dataset.status === statusDashboard));
    });
}

function atualizarAcoesMultiplas(tarefasVisiveis) {
    const idsVisiveis = new Set(tarefasVisiveis.map((tarefa) => tarefa.id));
    const selecionadasVisiveis = [...tarefasSelecionadas].filter((id) => idsVisiveis.has(id));
    acoesMultiplas.hidden = tarefasVisiveis.length === 0;
    quantidadeSelecionadas.textContent = selecionadasVisiveis.length
        ? `${selecionadasVisiveis.length} selecionada(s)`
        : "";
    selecionarTodas.checked = tarefasVisiveis.length > 0
        && selecionadasVisiveis.length === tarefasVisiveis.length;
    selecionarTodas.indeterminate = selecionadasVisiveis.length > 0
        && selecionadasVisiveis.length < tarefasVisiveis.length;
}

function limparSelecao() {
    tarefasSelecionadas.clear();
    selecionarTodas.checked = false;
    selecionarTodas.indeterminate = false;
}

function atualizarResumoDashboard() {
    const totalTodo = tarefas.filter((tarefa) => tarefa.status === "TODO").length;
    const totalDoing = tarefas.filter((tarefa) => tarefa.status === "DOING").length;
    const totalDone = tarefas.filter((tarefa) => tarefa.status === "DONE").length;

    const resumoTodo = document.getElementById("resumoTodo");
    const resumoDoing = document.getElementById("resumoDoing");
    const resumoDone = document.getElementById("resumoDone");

    if (resumoTodo) resumoTodo.textContent = totalTodo;
    if (resumoDoing) resumoDoing.textContent = totalDoing;
    if (resumoDone) resumoDone.textContent = totalDone;
}

function configurarAlarme(tarefa, horario) {
    if (!horario) return;

    const agora = new Date();
    const [hora, minuto] = horario.split(":").map(Number);
    const horarioAlarme = new Date();

    horarioAlarme.setHours(hora, minuto, 0, 0);

    if (horarioAlarme <= agora) {
        horarioAlarme.setDate(horarioAlarme.getDate() + 1);
    }

    const tempoAteAlarme = horarioAlarme.getTime() - agora.getTime();

    setTimeout(() => {
        alert(`ALARME DE TAREFA\n\nTarefa: ${tarefa.nome}\nDescrição: ${tarefa.descricao}`);
    }, tempoAteAlarme);
}

function obterDadosFormulario() {
    return {
        nome: document.getElementById("nome").value,
        descricao: document.getElementById("descricao").value,
        dataTermino: document.getElementById("dataTermino").value,
        prioridade: Number(document.getElementById("prioridade").value),
        categoria: document.getElementById("categoria").value,
        status: document.getElementById("status").value,
        alarme: document.getElementById("alarme").value
    };
}

function preencherFormulario(tarefa) {
    document.getElementById("nome").value = tarefa.nome;
    document.getElementById("descricao").value = tarefa.descricao;
    document.getElementById("dataTermino").value = tarefa.dataTermino;
    document.getElementById("prioridade").value = tarefa.prioridade;
    document.getElementById("categoria").value = tarefa.categoria;
    document.getElementById("status").value = tarefa.status;
    document.getElementById("alarme").value = tarefa.alarme || "";
}

function validarFormulario(dados) {
    if (!prioridadeValida(dados.prioridade)) {
        mostrarMensagem("A prioridade deve estar entre 1 e 5.", "erro");
        return false;
    }
    if (!dataFormatoValido(dados.dataTermino)) {
        mostrarMensagem("A data de término possui um formato inválido.", "erro");
        return false;
    }
    if (!dataNaoAnteriorHoje(dados.dataTermino)) {
        mostrarMensagem("A data de término não pode ser anterior à data atual.", "erro");
        return false;
    }
    return true;
}

function atualizarTarefaExistente(dados) {
    const tarefa = tarefas.find((item) => item.id === tarefaEmEdicao);
    if (tarefa) {
        Object.assign(tarefa, dados);
    }

    tarefaEmEdicao = null;
    botaoFormulario.textContent = "Adicionar tarefa";
    botaoCancelarEdicao.hidden = true;
    mostrarMensagem("Tarefa atualizada com sucesso!", "sucesso");
}

function adicionarNovaTarefa(dados) {
    const novaTarefa = { id: Date.now(), ...dados };
    tarefas.push(novaTarefa);
    configurarAlarme(novaTarefa, novaTarefa.alarme);
    mostrarMensagem("Tarefa adicionada com sucesso!", "sucesso");
}

export function iniciarCadastro() {
    formTarefa.addEventListener("submit", (event) => {
        event.preventDefault();

        const dadosTarefa = obterDadosFormulario();
        if (!validarFormulario(dadosTarefa)) return;

        tarefas = carregarTarefas();

        if (tarefaEmEdicao !== null) {
            atualizarTarefaExistente(dadosTarefa);
        } else {
            adicionarNovaTarefa(dadosTarefa);
        }

        salvarTarefas();
        listarTarefas();
        formTarefa.reset();
    });
}

export function iniciarEdicao(id) {
    const tarefa = tarefas.find((item) => item.id === id);
    if (!tarefa) return;

    tarefaEmEdicao = id;
    preencherFormulario(tarefa);

    botaoFormulario.textContent = "Salvar alterações";
    botaoCancelarEdicao.hidden = false;

    document.getElementById("cadastrar").classList.add("ativa");
    document.getElementById("tarefas").classList.remove("ativa");
    document.querySelectorAll(".menu-item").forEach((item) => item.classList.remove("ativo"));
    document.querySelector('[data-secao="cadastrar"]').classList.add("ativo");

    window.scrollTo({ top: 0, behavior: "smooth" });
}

export function iniciarCancelamentoEdicao() {
    botaoCancelarEdicao.addEventListener("click", () => {
        tarefaEmEdicao = null;
        formTarefa.reset();
        botaoFormulario.textContent = "Adicionar tarefa";
        botaoCancelarEdicao.hidden = true;
    });
}

export function iniciarRemocao(id) {
    const tarefa = tarefas.find((item) => item.id === id);
    if (!tarefa) return;

    tarefaPendenteRemocao = id;
    mensagemModalConfirmacao.textContent = `A tarefa “${tarefa.nome}” será removida permanentemente.`;
    modalConfirmacao.showModal();
}

export function iniciarAcoesDosCards() {
    buscaTarefas.addEventListener("input", () => {
        limparSelecao();
        listarTarefas();
    });

    listaTarefas.addEventListener("click", (event) => {
        const botaoEditar = event.target.closest(".btn-editar");
        const botaoRemover = event.target.closest(".btn-remover");

        if (botaoEditar) {
            iniciarEdicao(Number(botaoEditar.dataset.id));
        }

        if (botaoRemover) {
            iniciarRemocao(Number(botaoRemover.dataset.id));
        }
    });

    listaTarefas.addEventListener("change", (event) => {
        const checkbox = event.target.closest(".checkbox-tarefa");
        if (!checkbox) return;

        const id = Number(checkbox.value);
        if (checkbox.checked) {
            tarefasSelecionadas.add(id);
        } else {
            tarefasSelecionadas.delete(id);
        }
        atualizarAcoesMultiplas(tarefasFiltradasVisiveis());
    });

    botaoCancelarRemocao.addEventListener("click", () => modalConfirmacao.close());
    botaoConfirmarRemocao.addEventListener("click", () => {
        if (tarefaPendenteRemocao === null) return;
        tarefas = carregarTarefas().filter((tarefa) => tarefa.id !== tarefaPendenteRemocao);
        tarefasSelecionadas.delete(tarefaPendenteRemocao);
        salvarTarefas();
        modalConfirmacao.close();
        tarefaPendenteRemocao = null;
        listarTarefas();
        mostrarMensagem("Tarefa removida com sucesso!", "removido");
    });
    modalConfirmacao.addEventListener("close", () => {
        tarefaPendenteRemocao = null;
    });
    modalConfirmacao.addEventListener("click", (event) => {
        if (event.target === modalConfirmacao) modalConfirmacao.close();
    });
}

function tarefasFiltradasVisiveis() {
    const statusTarefas = statusDashboard
        ? tarefas.filter((tarefa) => tarefa.status === statusDashboard)
        : tarefas;
    const busca = buscaTarefas.value.trim().toLocaleLowerCase("pt-BR");
    return statusTarefas.filter((tarefa) => {
        const texto = `${tarefa.nome} ${tarefa.descricao} ${tarefa.categoria}`.toLocaleLowerCase("pt-BR");
        return texto.includes(busca);
    });
}

export function iniciarStatusMultiplo() {
    botaoStatusMultiplo.addEventListener("click", () => {
        tarefas = carregarTarefas();
        const novoStatus = statusMultiplo.value;

        if (!novoStatus) {
            mostrarMensagem("Selecione um status.", "erro");
            return;
        }

        if (tarefasSelecionadas.size === 0) {
            mostrarMensagem("Selecione pelo menos uma tarefa.", "erro");
            return;
        }

        tarefasSelecionadas.forEach((id) => {
            const tarefa = tarefas.find((item) => item.id === id);
            if (tarefa) {
                tarefa.status = novoStatus;
            }
        });

        limparSelecao();
        salvarTarefas();
        listarTarefas();
        statusMultiplo.value = "";

        mostrarMensagem("Status das tarefas atualizado com sucesso!", "sucesso");
    });
}

export function iniciarSelecionarTodas() {
    selecionarTodas.addEventListener("change", () => {
        const checkboxes = listaTarefas.querySelectorAll(".checkbox-tarefa");
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selecionarTodas.checked;
            const id = Number(checkbox.value);
            if (checkbox.checked) {
                tarefasSelecionadas.add(id);
            } else {
                tarefasSelecionadas.delete(id);
            }
        });
        atualizarAcoesMultiplas(tarefasFiltradasVisiveis());
    });
}