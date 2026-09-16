const filtroCategoria =
    document.getElementById("filtroCategoria");

const filtroPrioridade =
    document.getElementById("filtroPrioridade");

const filtroStatus =
    document.getElementById("filtroStatus");

const botaoFiltrar =
    document.getElementById("botaoFiltrar");

const botaoLimparFiltros =
    document.getElementById("botaoLimparFiltros");

const resultadoFiltros =
    document.getElementById("resultadoFiltros");

const ativarCategoria =
    document.getElementById("ativarCategoria");

const ativarPrioridade =
    document.getElementById("ativarPrioridade");

const ativarStatus =
    document.getElementById("ativarStatus");


let obterTarefas = null;


export function definirTarefas(
    funcaoObterTarefas
) {
    obterTarefas = funcaoObterTarefas;
}


function formatarData(data) {

    if (!data) {
        return "";
    }

    const [ano, mes, dia] =
        data.split("-");

    return `${dia}/${mes}/${ano}`;
}


function criarCardFiltro(tarefa) {

    const card =
        document.createElement("div");

    card.classList.add(
        "tarefa",
        `status-${tarefa.status}`
    );

    card.dataset.tarefaId =
        tarefa.id;

    card.innerHTML = `
        <div class="titulo-tarefa">

            <input
                type="checkbox"
                class="checkbox-tarefa"
                value="${tarefa.id}"
                aria-label="Selecionar tarefa ${tarefa.nome}"
            >

            <h3>${tarefa.nome}</h3>

        </div>

        <p>
            <strong>Descrição:</strong>
            ${tarefa.descricao}
        </p>

        <p>
            <strong>Data de término:</strong>
            ${formatarData(tarefa.dataTermino)}
        </p>

        <p>
            <strong>Prioridade:</strong>
            ${tarefa.prioridade}/5
        </p>

        <p>
            <strong>Categoria:</strong>
            ${tarefa.categoria}
        </p>

        <p>
            <strong>Status:</strong>
            ${tarefa.status}
        </p>

        ${
        tarefa.alarme
            ? `<p>
                    <strong>Alarme:</strong>
                    ${tarefa.alarme}
                   </p>`
            : `<p>
                    <strong>Alarme:</strong>
                    Não configurado
                   </p>`
    }
    `;

    return card;
}


function mostrarResultadoFiltro(
    resultado
) {

    resultadoFiltros.innerHTML = "";

    const quantidade =
        document.createElement("p");

    quantidade.textContent =
        `${resultado.length} tarefa(s) encontrada(s).`;

    quantidade.classList.add(
        "quantidade-resultados"
    );

    resultadoFiltros.appendChild(
        quantidade
    );

    if (resultado.length === 0) {
        return;
    }

    resultado.forEach(
        (tarefa) => {

            resultadoFiltros.appendChild(
                criarCardFiltro(tarefa)
            );
        }
    );
}


function filtrarTarefas() {

    const tarefas = obterTarefas();

    const categoria =
        filtroCategoria.value
            .trim()
            .toLowerCase();

    const prioridade =
        filtroPrioridade.value;

    const status =
        filtroStatus.value;


    const resultado =
        tarefas.filter(
            (tarefa) => {

                const correspondeCategoria =
                    !ativarCategoria.checked ||
                    tarefa.categoria
                        .toLowerCase() === categoria;

                const correspondePrioridade =
                    !ativarPrioridade.checked ||
                    tarefa.prioridade ===
                    Number(prioridade);

                const correspondeStatus =
                    !ativarStatus.checked ||
                    tarefa.status === status;

                return (
                    correspondeCategoria &&
                    correspondePrioridade &&
                    correspondeStatus
                );
            }
        );

    mostrarResultadoFiltro(
        resultado
    );
}


export function iniciarFiltros() {

    ativarCategoria.addEventListener(
        "change",
        function () {
            filtroCategoria.disabled =
                !this.checked;
        }
    );

    ativarPrioridade.addEventListener(
        "change",
        function () {
            filtroPrioridade.disabled =
                !this.checked;
        }
    );

    ativarStatus.addEventListener(
        "change",
        function () {
            filtroStatus.disabled =
                !this.checked;
        }
    );


    botaoFiltrar.addEventListener(
        "click",
        () => {
            filtrarTarefas();
        }
    );


    botaoLimparFiltros.addEventListener(
        "click",
        () => {

            filtroCategoria.value = "";
            filtroPrioridade.value = "";
            filtroStatus.value = "";

            ativarCategoria.checked = false;
            ativarPrioridade.checked = false;
            ativarStatus.checked = false;

            filtroCategoria.disabled = true;
            filtroPrioridade.disabled = true;
            filtroStatus.disabled = true;

            resultadoFiltros.innerHTML = "";
        }
    );
}