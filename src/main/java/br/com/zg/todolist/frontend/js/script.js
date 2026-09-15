const formTarefa = document.getElementById("formTarefa");
const listaTarefas = document.getElementById("listaTarefas");
const botaoFormulario = document.getElementById("botaoFormulario");
const botaoCancelarEdicao = document.getElementById("botaoCancelarEdicao");
const filtroCategoria = document.getElementById("filtroCategoria");
const filtroPrioridade = document.getElementById("filtroPrioridade");
const filtroStatus = document.getElementById("filtroStatus");
const botaoFiltrar = document.getElementById("botaoFiltrar");
const botaoLimparFiltros = document.getElementById("botaoLimparFiltros");
const resultadoFiltros = document.getElementById("resultadoFiltros");
const ativarCategoria = document.getElementById("ativarCategoria");
const ativarPrioridade = document.getElementById("ativarPrioridade");
const ativarStatus = document.getElementById("ativarStatus");

let tarefas = JSON.parse(localStorage.getItem("tarefas")) || [];
let tarefaEmEdicao = null;

function salvarTarefas() {
    localStorage.setItem("tarefas", JSON.stringify(tarefas));
}

function listarTarefas() {
    listaTarefas.innerHTML = "";

    if (tarefas.length === 0) {
        listaTarefas.innerHTML = "<p>Nenhuma tarefa cadastrada.</p>";
        return;
    }

    tarefas.forEach((tarefa) => {

        const card = document.createElement("div");
        card.classList.add("tarefa", `status-${tarefa.status}`);

        card.innerHTML = `
            <h3>${tarefa.nome}</h3>
            <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
            <p><strong>Data de término:</strong> ${tarefa.dataTermino}</p>
            <p><strong>Prioridade:</strong> ${tarefa.prioridade}</p>
            <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
            <p><strong>Status:</strong> ${tarefa.status}</p>
            ${
            tarefa.alarme
                ? `<p><strong>Alarme:</strong> ${tarefa.alarme} minuto(s)</p>`
                : `<p><strong>Alarme:</strong> Não configurado</p>`
        }
            
            <div class="acoes-tarefa">
            <button onclick="editarTarefa(${tarefa.id})">
                Editar
            </button>
            
            <button onclick="removerTarefa(${tarefa.id})">
                Remover
            </button>
            </div>
            `;

        listaTarefas.appendChild(card);
    });
}

function configurarAlarme(tarefa, horario) {

    if (!horario) {
        return;
    }

    const agora = new Date();
    const [hora, minuto] = horario.split(":").map(Number);

    const horarioAlarme = new Date();

    horarioAlarme.setHours(hora);
    horarioAlarme.setMinutes(minuto);
    horarioAlarme.setSeconds(0);
    horarioAlarme.setMilliseconds(0);

    if (horarioAlarme <= agora) {
        horarioAlarme.setDate(horarioAlarme.getDate() + 1);
    }

    const tempoAteAlarme =
        horarioAlarme.getTime() - agora.getTime();

    setTimeout(() => {
        alert(
            `ALARME DE TAREFA\n\n` +
            `Tarefa: ${tarefa.nome}\n` +
            `Descrição: ${tarefa.descricao}`
        );
    }, tempoAteAlarme);
}


formTarefa.addEventListener("submit", function (event) {

    event.preventDefault();

    const dadosTarefa = {
        nome: document.getElementById("nome").value,
        descricao: document.getElementById("descricao").value,
        dataTermino: document.getElementById("dataTermino").value,
        prioridade: Number(document.getElementById("prioridade").value),
        categoria: document.getElementById("categoria").value,
        status: document.getElementById("status").value,
        alarme: document.getElementById("alarme").value
    };

    if (tarefaEmEdicao !== null) {

        const tarefa = tarefas.find(
            (tarefa) => tarefa.id === tarefaEmEdicao
        );

        if (tarefa) {
            tarefa.nome = dadosTarefa.nome;
            tarefa.descricao = dadosTarefa.descricao;
            tarefa.dataTermino = dadosTarefa.dataTermino;
            tarefa.prioridade = dadosTarefa.prioridade;
            tarefa.categoria = dadosTarefa.categoria;
            tarefa.status = dadosTarefa.status;
            tarefa.alarme = dadosTarefa.alarme;
        }

        tarefaEmEdicao = null;

        botaoFormulario.textContent = "Adicionar tarefa";
        botaoCancelarEdicao.hidden = true;

    } else {

        const novaTarefa = {
            id: Date.now(),
            ...dadosTarefa
        };

        tarefas.push(novaTarefa);
    }

    salvarTarefas();
    listarTarefas();

    formTarefa.reset();
});

botaoCancelarEdicao.addEventListener("click", function () {

    tarefaEmEdicao = null;

    formTarefa.reset();

    botaoFormulario.textContent = "Adicionar tarefa";
    botaoCancelarEdicao.hidden = true;
});


const botoesMenu = document.querySelectorAll(".menu-item");
const secoes = document.querySelectorAll(".secao");

botoesMenu.forEach((botao) => {

    botao.addEventListener("click", () => {

        botoesMenu.forEach((item) => {
            item.classList.remove("ativo");
        });

        secoes.forEach((secao) => {
            secao.classList.remove("ativa");
        });

        botao.classList.add("ativo");

        const secao = document.getElementById(botao.dataset.secao);

        secao.classList.add("ativa");
    });
});

listarTarefas();

function removerTarefa(id) {

    const confirmar = confirm("Deseja realmente remover esta tarefa?");

    if (!confirmar) {
        return;
    }

    tarefas = tarefas.filter((tarefa) => tarefa.id !== id);

    salvarTarefas();
    listarTarefas();
}

function editarTarefa(id) {

    const tarefa = tarefas.find((tarefa) => tarefa.id === id);

    if (!tarefa) {
        return;
    }

    tarefaEmEdicao = id;

    document.getElementById("nome").value = tarefa.nome;
    document.getElementById("descricao").value = tarefa.descricao;
    document.getElementById("dataTermino").value = tarefa.dataTermino;
    document.getElementById("prioridade").value = tarefa.prioridade;
    document.getElementById("categoria").value = tarefa.categoria;
    document.getElementById("status").value = tarefa.status;
    document.getElementById("alarme").value = tarefa.alarme || "";

    botaoFormulario.textContent = "Salvar alterações";
    botaoCancelarEdicao.hidden = false;

    document.getElementById("cadastrar").classList.add("ativa");
    document.getElementById("tarefas").classList.remove("ativa");

    document.querySelectorAll(".menu-item").forEach((item) => {
        item.classList.remove("ativo");
    });

    document.querySelector('[data-secao="cadastrar"]').classList.add("ativo");

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

ativarCategoria.addEventListener("change", function () {
    filtroCategoria.disabled = !this.checked;
});

ativarPrioridade.addEventListener("change", function () {
    filtroPrioridade.disabled = !this.checked;
});

ativarStatus.addEventListener("change", function () {
    filtroStatus.disabled = !this.checked;
});

function filtrarTarefas() {

    const categoria = filtroCategoria.value.trim().toLowerCase();
    const prioridade = filtroPrioridade.value;
    const status = filtroStatus.value;

    const resultado = tarefas.filter((tarefa) => {

        const correspondeCategoria =
            !ativarCategoria.checked ||
            tarefa.categoria.toLowerCase() === categoria;

        const correspondePrioridade =
            !ativarPrioridade.checked ||
            tarefa.prioridade === Number(prioridade);

        const correspondeStatus =
            !ativarStatus.checked ||
            tarefa.status === status;

        return (
            correspondeCategoria &&
            correspondePrioridade &&
            correspondeStatus
        );
    });

    mostrarResultadoFiltro(resultado);
}

function mostrarResultadoFiltro(resultado) {

    resultadoFiltros.innerHTML = "";

    if (resultado.length === 0) {
        resultadoFiltros.innerHTML =
            "<p>Nenhuma tarefa encontrada.</p>";
        return;
    }

    resultado.forEach((tarefa) => {

        const card = document.createElement("div");
        card.classList.add("tarefa");

        card.innerHTML = `
            <h3>${tarefa.nome}</h3>
            <p><strong>Descrição:</strong> ${tarefa.descricao}</p>
            <p><strong>Data de término:</strong> ${tarefa.dataTermino}</p>
            <p><strong>Prioridade:</strong> ${tarefa.prioridade}</p>
            <p><strong>Categoria:</strong> ${tarefa.categoria}</p>
            <p><strong>Status:</strong> ${tarefa.status}</p>
            ${
            tarefa.alarme
                ? `<p><strong>Alarme:</strong> ${tarefa.alarme} minuto(s)</p>`
                : `<p><strong>Alarme:</strong> Não configurado</p>`
        }
        `;

        resultadoFiltros.appendChild(card);
    });
}

botaoFiltrar.addEventListener("click", function () {
    filtrarTarefas();
});

botaoLimparFiltros.addEventListener("click", function () {

    filtroCategoria.value = "";
    filtroPrioridade.value = "";
    filtroStatus.value = "";

    resultadoFiltros.innerHTML = "";
});