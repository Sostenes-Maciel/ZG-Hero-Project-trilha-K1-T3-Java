const formTarefa = document.getElementById("formTarefa");
const listaTarefas = document.getElementById("listaTarefas");

let tarefas = JSON.parse(localStorage.getItem("tarefas")) || [];

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


formTarefa.addEventListener("submit", function (event) {
    event.preventDefault();

    const novaTarefa = {
        id: Date.now(),
        nome: document.getElementById("nome").value,
        descricao: document.getElementById("descricao").value,
        dataTermino: document.getElementById("dataTermino").value,
        prioridade: Number(document.getElementById("prioridade").value),
        categoria: document.getElementById("categoria").value,
        status: "TODO",
        alarme: document.getElementById("alarme").value
    };

    tarefas.push(novaTarefa);

    salvarTarefas();
    listarTarefas();

    formTarefa.reset();
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

    const nome = prompt("Nome:", tarefa.nome);

    if (nome === null) {
        return;
    }

    const descricao = prompt("Descrição:", tarefa.descricao);

    if (descricao === null) {
        return;
    }

    const dataTermino = prompt(
        "Data de término (AAAA-MM-DD):",
        tarefa.dataTermino
    );

    if (dataTermino === null) {
        return;
    }

    const prioridade = prompt(
        "Prioridade (1 a 5):",
        tarefa.prioridade
    );

    if (prioridade === null) {
        return;
    }

    const categoria = prompt(
        "Categoria:",
        tarefa.categoria
    );

    if (categoria === null) {
        return;
    }

    tarefa.nome = nome;
    tarefa.descricao = descricao;
    tarefa.dataTermino = dataTermino;
    tarefa.prioridade = Number(prioridade);
    tarefa.categoria = categoria;

    salvarTarefas();
    listarTarefas();
}