const filtroCategoria = document.getElementById("filtroCategoria");
const filtroPrioridade = document.getElementById("filtroPrioridade");
const filtroStatus = document.getElementById("filtroStatus");

const botaoFiltrar = document.getElementById("botaoFiltrar");
const botaoLimparFiltros = document.getElementById("botaoLimparFiltros");
const resultadoFiltros = document.getElementById("resultadoFiltros");

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
        </div>
        <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
        <p><strong>Data de término:</strong> ${formatarData(tarefa.dataTermino)}</p>
        <p><strong>Prioridade:</strong> ${tarefa.prioridade}/5</p>
        <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
        <p><strong>Status:</strong> ${tarefa.status}</p>
        ${infoAlarme}
    `;

    return card;
}

function mostrarResultadoFiltro(tarefasFiltradas) {
    resultadoFiltros.innerHTML = "";

    const quantidade = document.createElement("p");
    quantidade.textContent = `${tarefasFiltradas.length} tarefa(s) encontrada(s).`;
    quantidade.classList.add("quantidade-resultados");
    resultadoFiltros.appendChild(quantidade);

    tarefasFiltradas.forEach((tarefa) => {
        resultadoFiltros.appendChild(criarCardFiltro(tarefa));
    });
}

function filtrarTarefas() {
    if (!obterTarefas) return;

    const tarefas = obterTarefas();
    const categoria = filtroCategoria.value.trim().toLowerCase();
    const prioridade = Number(filtroPrioridade.value);
    const status = filtroStatus.value;

    const tarefasFiltradas = tarefas.filter((tarefa) => {
        const correspondeCategoria = !ativarCategoria.checked || tarefa.categoria.toLowerCase() === categoria;
        const correspondePrioridade = !ativarPrioridade.checked || tarefa.prioridade === prioridade;
        const correspondeStatus = !ativarStatus.checked || tarefa.status === status;

        return correspondeCategoria && correspondePrioridade && correspondeStatus;
    });

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

export function iniciarFiltros() {
    configurarToggleFiltro(ativarCategoria, filtroCategoria);
    configurarToggleFiltro(ativarPrioridade, filtroPrioridade);
    configurarToggleFiltro(ativarStatus, filtroStatus);

    botaoFiltrar.addEventListener("click", filtrarTarefas);

    botaoLimparFiltros.addEventListener("click", () => {
        limparFiltro(ativarCategoria, filtroCategoria);
        limparFiltro(ativarPrioridade, filtroPrioridade);
        limparFiltro(ativarStatus, filtroStatus);

        resultadoFiltros.innerHTML = "";
    });
}