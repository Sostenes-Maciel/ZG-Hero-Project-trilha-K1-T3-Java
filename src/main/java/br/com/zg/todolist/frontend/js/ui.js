const mensagem = document.getElementById("mensagem");
const botaoContraste = document.getElementById("botaoContraste");
let temporizadorFechamentoMenu;

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
    const menu = document.querySelector(".menu");
    const botaoMenu = document.getElementById("botaoMenu");

    botoesMenu.forEach((botao) => {
        if (!botao.dataset.secao) return;

        botao.addEventListener("click", () => {
            alternarSecaoAtiva(botoesMenu, secoes, botao);
            fecharMenuSuspenso(menu, botaoMenu);
            if (botao.dataset.secao === "tarefas" || botao.dataset.secao === "inicio") {
                window.dispatchEvent(new CustomEvent("tarefas:mostrar-todas"));
            }
        });
    });

    configurarBuscaNavegacao();

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

    botaoMenu?.addEventListener("click", () => {
        if (!menu) return;
        const aberto = !menu.classList.contains("menu-aberto");
        if (aberto) {
            clearTimeout(temporizadorFechamentoMenu);
            menu.classList.remove("menu-fechando");
            menu.classList.add("menu-aberto");
        } else {
            fecharMenuSuspenso(menu, botaoMenu);
        }
        botaoMenu.setAttribute("aria-expanded", String(aberto));
        botaoMenu.setAttribute("aria-label", aberto ? "Fechar menu" : "Abrir menu");
    });

    document.querySelectorAll("[data-ir-para]").forEach((botao) => {
        botao.addEventListener("click", () => {
            document.querySelector(`.menu-item[data-secao="${botao.dataset.irPara}"]`)?.click();
        });
    });

    configurarCarrossel();
    configurarAnimacoesAoRolar();
}

function configurarBuscaNavegacao() {
    const formularioBusca = document.getElementById("buscaNavegacao");
    const campoBusca = document.getElementById("termoNavegacao");
    if (!formularioBusca || !campoBusca) return;

    const destinos = [
        { secao: "cadastrar", termos: ["cadastro", "cadastrar", "nova tarefa", "criar tarefa"] },
        { secao: "tarefas", termos: ["tarefas", "minhas tarefas", "lista"] },
        { secao: "filtros", termos: ["filtro", "filtrar", "busca"] },
        { secao: "inicio", termos: ["inicio", "home", "principal"] }
    ];

    formularioBusca.addEventListener("submit", (evento) => {
        evento.preventDefault();
        const termo = campoBusca.value.trim().normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "").toLowerCase();
        if (!termo) {
            mostrarMensagem("Digite uma página, como cadastro ou tarefas.", "erro");
            campoBusca.focus();
            return;
        }

        const destino = destinos.find(({ termos }) => termos.some((palavra) => termo.includes(palavra)));
        const botaoDestino = destino
            && document.querySelector(`.menu-item[data-secao="${destino.secao}"]`);

        if (!botaoDestino) {
            mostrarMensagem("Não encontrei essa página. Tente cadastro, tarefas ou filtros.", "erro");
            campoBusca.focus();
            return;
        }

        botaoDestino.click();
        campoBusca.value = "";
    });
}

function fecharMenuSuspenso(menu, botaoMenu) {
    if (!menu || !botaoMenu) return;
    menu.classList.remove("menu-aberto");
    menu.classList.add("menu-fechando");
    clearTimeout(temporizadorFechamentoMenu);
    temporizadorFechamentoMenu = setTimeout(() => {
        menu.classList.remove("menu-fechando");
    }, 280);
    botaoMenu.setAttribute("aria-expanded", "false");
    botaoMenu.setAttribute("aria-label", "Abrir menu");
}

function configurarCarrossel() {
    const carrossel = document.querySelector(".carrossel");
    if (!carrossel) return;

    const slides = [...carrossel.querySelectorAll(".carrossel-slide")];
    const pontos = [...carrossel.querySelectorAll(".carrossel-ponto")];
    if (slides.length < 2 || pontos.length !== slides.length) return;

    let indiceAtual = 0;
    let temporizadorCarrossel;

    function mostrarSlide(indice) {
        indiceAtual = (indice + slides.length) % slides.length;
        slides.forEach((slide, i) => {
            const ativo = i === indiceAtual;
            slide.classList.toggle("ativo", ativo);
            slide.setAttribute("aria-hidden", String(!ativo));
            pontos[i].classList.toggle("ativo", ativo);
            pontos[i].setAttribute("aria-current", String(ativo));
        });
    }

    function pausarCarrossel() {
        window.clearInterval(temporizadorCarrossel);
    }

    function iniciarCarrossel() {
        pausarCarrossel();
        if (!window.matchMedia("(prefers-reduced-motion: reduce)").matches
            && !document.hidden
            && !carrossel.matches(":hover")
            && !carrossel.contains(document.activeElement)) {
            temporizadorCarrossel = window.setInterval(() => {
                mostrarSlide(indiceAtual + 1);
            }, 5000);
        }
    }

    carrossel.querySelector(".carrossel-anterior")
        .addEventListener("click", () => {
            mostrarSlide(indiceAtual - 1);
            iniciarCarrossel();
        });
    carrossel.querySelector(".carrossel-proximo")
        .addEventListener("click", () => {
            mostrarSlide(indiceAtual + 1);
            iniciarCarrossel();
        });
    pontos.forEach((ponto, indice) => {
        ponto.addEventListener("click", () => {
            mostrarSlide(indice);
            iniciarCarrossel();
        });
    });

    carrossel.addEventListener("mouseenter", pausarCarrossel);
    carrossel.addEventListener("mouseleave", iniciarCarrossel);
    carrossel.addEventListener("focusin", pausarCarrossel);
    carrossel.addEventListener("focusout", iniciarCarrossel);
    document.addEventListener("visibilitychange", iniciarCarrossel);
    iniciarCarrossel();
}

function configurarAnimacoesAoRolar() {
    const elementos = document.querySelectorAll(".revelar");
    if (!("IntersectionObserver" in window)) {
        elementos.forEach((elemento) => elemento.classList.add("visivel"));
        return;
    }

    const observador = new IntersectionObserver((entradas, observer) => {
        entradas.forEach((entrada) => {
            if (!entrada.isIntersecting) return;
            entrada.target.classList.add("visivel");
            observer.unobserve(entrada.target);
        });
    }, { threshold: 0.12 });

    elementos.forEach((elemento) => observador.observe(elemento));
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