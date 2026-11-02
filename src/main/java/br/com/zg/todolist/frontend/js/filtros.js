const filtroCategoria = document.getElementById("filtroCategoria");
const filtroPrioridade = document.getElementById("filtroPrioridade");
const filtroStatus = document.getElementById("filtroStatus");

const botaoFiltrar = document.getElementById("botaoFiltrar");
const botaoLimparFiltros = document.getElementById("botaoLimparFiltros");
const resultadoFiltros = document.getElementById("resultadoFiltros");
const filtrosAtivos = document.getElementById("filtrosAtivos");

const ativarCategoria = document.getElementById("ativarCategoria");
const ativarPrioridade = document.getElementById("ativarPrioridade");
const ativarStatus = document.getElementById("ativarStatus");

let obterTarefas = null;

export function definirTarefas(funcaoObterTarefas) {
    obterTarefas = funcaoObterTarefas;
}

function formatarData(data) {
    if (!data) return "";

    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}

function descricaoStatus(status) {
    const descricoes = {
        TODO: "A fazer",
        DOING: "Em andamento",
        DONE: "Concluída"
    };

    return descricoes[status] || status;
}

function criarCardFiltro(tarefa) {
    const card = document.createElement("div");
    card.classList.add("tarefa", `status-${tarefa.status}`);
    card.dataset.tarefaId = tarefa.id;

    const infoAlarme = tarefa.alarme
        ? `<p><strong>Alarme:</strong> ${tarefa.alarme}</p>`
        : `<p><strong>Alarme:</strong> Não configurado</p>`;

    card.innerHTML = `
        <div class="titulo-tarefa">
            <input type="checkbox" class="checkbox-tarefa" value="${tarefa.id}" aria-label="Selecionar tarefa ${tarefa.nome}">
            <h3>${tarefa.nome}</h3>
            <span class="status-indicador">${descricaoStatus(tarefa.status)}</span>
        </div>
        <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
        <p><strong>Data de término:</strong> ${formatarData(tarefa.dataTermino)}</p>
        <p><strong>Prioridade:</strong> ${tarefa.prioridade}/5</p>
        <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
        ${infoAlarme}
    `;

    return card;
}

function mostrarResultadoFiltro(tarefasFiltradas) {
    resultadoFiltros.innerHTML = "";

    if (tarefasFiltradas === null) {
        mostrarEstadoFiltro("Ative um ou mais filtros para encontrar tarefas específicas.");
        return;
    }

    if (tarefasFiltradas.length === 0) {
        mostrarEstadoFiltro("Nenhuma tarefa corresponde aos filtros selecionados.", "Limpar filtros");
        return;
    }

    const quantidade = document.createElement("p");
    quantidade.textContent = `${tarefasFiltradas.length} tarefa(s) encontrada(s).`;
    quantidade.classList.add("quantidade-resultados");
    resultadoFiltros.appendChild(quantidade);

    tarefasFiltradas.forEach((tarefa) => {
        resultadoFiltros.appendChild(criarCardFiltro(tarefa));
    });
}

function mostrarEstadoFiltro(texto, textoAcao) {
    const estado = document.createElement("div");
    estado.className = "lista-vazia";
    const mensagem = document.createElement("p");
    mensagem.textContent = texto;
    estado.appendChild(mensagem);

    if (textoAcao) {
        const acao = document.createElement("button");
        acao.type = "button";
        acao.textContent = textoAcao;
        acao.addEventListener("click", limparFiltros);
        estado.appendChild(acao);
    }

    resultadoFiltros.appendChild(estado);
}

function obterFiltrosAtivos() {
    const ativos = [];

    if (ativarCategoria.checked && filtroCategoria.value.trim()) {
        ativos.push({
            checkbox: ativarCategoria,
            input: filtroCategoria,
            rotulo: "Categoria",
            valor: filtroCategoria.value.trim(),
            corresponde: (tarefa) => tarefa.categoria.toLocaleLowerCase("pt-BR")
                === filtroCategoria.value.trim().toLocaleLowerCase("pt-BR")
        });
    }

    if (ativarPrioridade.checked && filtroPrioridade.value) {
        ativos.push({
            checkbox: ativarPrioridade,
            input: filtroPrioridade,
            rotulo: "Prioridade",
            valor: filtroPrioridade.value,
            corresponde: (tarefa) => tarefa.prioridade === Number(filtroPrioridade.value)
        });
    }

    if (ativarStatus.checked && filtroStatus.value) {
        ativos.push({
            checkbox: ativarStatus,
            input: filtroStatus,
            rotulo: "Status",
            valor: descricaoStatus(filtroStatus.value),
            corresponde: (tarefa) => tarefa.status === filtroStatus.value
        });
    }

    return ativos;
}

function renderizarFiltrosAtivos(ativos) {
    filtrosAtivos.innerHTML = "";
    ativos.forEach((filtro) => {
        const chip = document.createElement("button");
        chip.type = "button";
        chip.className = "filtro-ativo";
        chip.setAttribute("aria-label", `Remover filtro ${filtro.rotulo}: ${filtro.valor}`);
        chip.textContent = `${filtro.rotulo}: ${filtro.valor} ×`;
        chip.addEventListener("click", () => {
            filtro.checkbox.checked = false;
            filtro.input.value = "";
            filtro.input.disabled = true;
            filtrarTarefas();
        });
        filtrosAtivos.appendChild(chip);
    });
}

function filtrarTarefas() {
    if (!obterTarefas) return;

    const tarefas = obterTarefas();
    const ativos = obterFiltrosAtivos();
    renderizarFiltrosAtivos(ativos);
    const tarefasFiltradas = tarefas.filter((tarefa) =>
        ativos.every((filtro) => filtro.corresponde(tarefa))
    );

    mostrarResultadoFiltro(tarefasFiltradas);
}

// --- MÉTODOS PRIVADOS (Auxiliares) ---

function configurarToggleFiltro(checkbox, inputElement) {
    checkbox.addEventListener("change", function () {
        inputElement.disabled = !this.checked;
    });
}

function limparFiltro(checkbox, inputElement) {
    checkbox.checked = false;
    inputElement.value = "";
    inputElement.disabled = true;
}

function limparFiltros() {
    limparFiltro(ativarCategoria, filtroCategoria);
    limparFiltro(ativarPrioridade, filtroPrioridade);
    limparFiltro(ativarStatus, filtroStatus);
    filtrosAtivos.innerHTML = "";
    mostrarResultadoFiltro(null);
}

export function iniciarFiltros() {
    configurarToggleFiltro(ativarCategoria, filtroCategoria);
    configurarToggleFiltro(ativarPrioridade, filtroPrioridade);
    configurarToggleFiltro(ativarStatus, filtroStatus);

    botaoFiltrar.addEventListener("click", filtrarTarefas);
    botaoLimparFiltros.addEventListener("click", limparFiltros);
    mostrarResultadoFiltro(null);
}