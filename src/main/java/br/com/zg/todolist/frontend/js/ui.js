const mensagem = document.getElementById("mensagem");
const botaoContraste = document.getElementById("botaoContraste");

export function mostrarMensagem(texto, tipo) {
    if (!mensagem) return;

    mensagem.textContent = texto;
    mensagem.className = `mensagem ${tipo}`;

    setTimeout(() => {
        mensagem.textContent = "";
        mensagem.className = "mensagem";
    }, 3000);
}

export function configurarMenu() {
    const botoesMenu = document.querySelectorAll(".menu-item");
    const secoes = document.querySelectorAll(".secao");

    botoesMenu.forEach((botao) => {
        if (!botao.dataset.secao) return;

        botao.addEventListener("click", () => {
            alternarSecaoAtiva(botoesMenu, secoes, botao);
            if (botao.dataset.secao === "tarefas") {
                window.dispatchEvent(new CustomEvent("tarefas:mostrar-todas"));
            }
        });
    });

    document.querySelectorAll(".stat-card[data-status]").forEach((card) => {
        card.addEventListener("click", () => {
            const botaoTarefas = document.querySelector('.menu-item[data-secao="tarefas"]');
            if (!botaoTarefas) return;

            alternarSecaoAtiva(botoesMenu, secoes, botaoTarefas);
            window.dispatchEvent(new CustomEvent("tarefas:filtrar-status", {
                detail: { status: card.dataset.status }
            }));
        });
    });
}

function alternarSecaoAtiva(botoesMenu, secoes, botaoClicado) {
    botoesMenu.forEach((item) => item.classList.remove("ativo"));
    secoes.forEach((secao) => {
        secao.classList.remove("ativa");
        secao.style.animation = "none";
        void secao.offsetWidth;
        secao.style.animation = "fadeUp 0.35s ease";
    });

    botaoClicado.classList.add("ativo");

    const secaoAlvo = document.getElementById(botaoClicado.dataset.secao);
    if (secaoAlvo) {
        secaoAlvo.classList.add("ativa");
    }
}

export function configurarAltoContraste() {
    if (!botaoContraste) return;

    // Aplica o estado salvo no LocalStorage ao carregar a página
    aplicarAltoContrasteSalvo();

    botaoContraste.addEventListener("click", alternarAltoContraste);
}

function alternarAltoContraste() {
    const corpoPagina = document.body;
    corpoPagina.classList.toggle("alto-contraste");

    const contrasteAtivo = corpoPagina.classList.contains("alto-contraste");

    localStorage.setItem("altoContraste", String(contrasteAtivo));
    atualizarTextoBotaoContraste(contrasteAtivo);
}

function aplicarAltoContrasteSalvo() {
    const contrasteSalvo = localStorage.getItem("altoContraste") === "true";

    if (contrasteSalvo) {
        document.body.classList.add("alto-contraste");
        atualizarTextoBotaoContraste(true);
    }
}

function atualizarTextoBotaoContraste(estaAtivo) {
    const rotulo = estaAtivo ? "Desativar alto contraste" : "Ativar alto contraste";
    botaoContraste.setAttribute("aria-label", rotulo);
    botaoContraste.title = rotulo;
    botaoContraste.setAttribute("aria-pressed", String(estaAtivo));
}