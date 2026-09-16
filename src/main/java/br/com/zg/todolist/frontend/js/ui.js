const mensagem = document.getElementById("mensagem");
const botaoContraste = document.getElementById("botaoContraste");

export function mostrarMensagem(texto, tipo) {
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

        if (!botao.dataset.secao) {
            return;
        }

        botao.addEventListener("click", () => {

            botoesMenu.forEach((item) => {
                item.classList.remove("ativo");
            });

            secoes.forEach((secao) => {
                secao.classList.remove("ativa");
            });

            botao.classList.add("ativo");

            const secao = document.getElementById(
                botao.dataset.secao
            );

            secao.classList.add("ativa");
        });
    });
}

export function configurarAltoContraste() {

    botaoContraste.addEventListener("click", () => {

        document.body.classList.toggle("alto-contraste");

        const contrasteAtivo =
            document.body.classList.contains("alto-contraste");

        localStorage.setItem(
            "altoContraste",
            String(contrasteAtivo)
        );

        botaoContraste.textContent =
            contrasteAtivo
                ? "Desativar alto contraste"
                : "Alto contraste";
    });

    const contrasteSalvo =
        localStorage.getItem("altoContraste") === "true";

    if (contrasteSalvo) {
        document.body.classList.add("alto-contraste");
        botaoContraste.textContent =
            "Desativar alto contraste";
    }
}