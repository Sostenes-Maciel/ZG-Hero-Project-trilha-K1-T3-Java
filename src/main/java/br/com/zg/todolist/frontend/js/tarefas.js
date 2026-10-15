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

let tarefas = carregarTarefas();
let tarefaEmEdicao = null;

window.addEventListener("storage", (evento) => {
    if (evento.key !== "tarefas") return;
    tarefas = evento.newValue ? JSON.parse(evento.newValue) : [];
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
                <button type="button" class="btn-editar" data-id="${tarefa.id}">Editar</button>
                <button type="button" class="btn-remover" data-id="${tarefa.id}">Remover</button>
           </div>`
        : "";

    card.innerHTML = `
        <div class="titulo-tarefa">
            <input type="checkbox" class="checkbox-tarefa" value="${tarefa.id}" aria-label="Selecionar tarefa ${tarefa.nome}">
            <h3>${tarefa.nome}</h3>
        </div>
        <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
        <p><strong>Data de término:</strong> ${formatarData(tarefa.dataTermino)}</p>
        <p><strong>Prioridade:</strong> ${tarefa.prioridade}/5</p>
        <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
        <p><strong>Status:</strong> ${tarefa.status}</p>
        ${infoAlarme}
        ${infoAcoes}
    `;

    return card;
}

export function listarTarefas() {
    listaTarefas.innerHTML = "";

    if (tarefas.length === 0) {
        listaTarefas.innerHTML = "<p>Nenhuma tarefa cadastrada.</p>";
        return;
    }

    tarefas.forEach((tarefa) => {
        listaTarefas.appendChild(criarCardTarefa(tarefa));
    });
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
    const card = document.querySelector(`[data-tarefa-id="${id}"]`);
    if (!card) return;

    const confirmacao = document.createElement("div");
    confirmacao.classList.add("confirmacao-remocao");
    confirmacao.innerHTML = `
        <p>Deseja realmente remover esta tarefa?</p>
        <div class="botoes-confirmacao">
            <button type="button" class="btn-confirmar-remocao">Remover</button>
            <button type="button" class="btn-cancelar-remocao">Cancelar</button>
        </div>
    `;

    card.appendChild(confirmacao);

    confirmacao.querySelector(".btn-confirmar-remocao").addEventListener("click", () => {
        tarefas = carregarTarefas();
        tarefas = tarefas.filter((tarefa) => tarefa.id !== id);

        salvarTarefas();
        listarTarefas();
        mostrarMensagem("Tarefa removida com sucesso!", "removido"); // ou "sucesso" dependendo da sua config UI
    });

    confirmacao.querySelector(".btn-cancelar-remocao").addEventListener("click", () => {
        confirmacao.remove();
    });
}

export function iniciarAcoesDosCards() {
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
}

export function iniciarStatusMultiplo() {
    botaoStatusMultiplo.addEventListener("click", () => {
        tarefas = carregarTarefas();
        const novoStatus = statusMultiplo.value;

        if (!novoStatus) {
            mostrarMensagem("Selecione um status.", "erro");
            return;
        }

        const selecionadas = document.querySelectorAll(".checkbox-tarefa:checked");
        if (selecionadas.length === 0) {
            mostrarMensagem("Selecione pelo menos uma tarefa.", "erro");
            return;
        }

        selecionadas.forEach((checkbox) => {
            const id = Number(checkbox.value);
            const tarefa = tarefas.find((item) => item.id === id);
            if (tarefa) {
                tarefa.status = novoStatus;
            }
        });

        salvarTarefas();
        listarTarefas();

        selecionadas.forEach(cb => cb.checked = false); // Correção UI
        selecionarTodas.checked = false;
        statusMultiplo.value = "";

        mostrarMensagem("Status das tarefas atualizado com sucesso!", "sucesso");
    });
}

export function iniciarSelecionarTodas() {
    selecionarTodas.addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".checkbox-tarefa");
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selecionarTodas.checked;
        });
    });
}