import { mostrarMensagem } from "./ui.js";

const ENDPOINT_NOTIFICACOES_EMAIL = "http://127.0.0.1:8081/api/notificacoes/email";
const botaoConfigurarEmail = document.getElementById("botaoConfigurarEmail");
const modalConfiguracaoEmail = document.getElementById("modalConfiguracaoEmail");
const formularioConfiguracaoEmail = document.getElementById("formularioConfiguracaoEmail");
const campoTokenApiEmail = document.getElementById("tokenApiEmail");
const botaoCancelarConfiguracaoEmail = document.getElementById("botaoCancelarConfiguracaoEmail");
let tokenApiConfigurado = "";

export function iniciarConfiguracaoNotificacoesEmail() {
    if (!botaoConfigurarEmail || !modalConfiguracaoEmail || !formularioConfiguracaoEmail) return;

    botaoConfigurarEmail.addEventListener("click", () => {
        campoTokenApiEmail.value = "";
        modalConfiguracaoEmail.showModal();
    });

    formularioConfiguracaoEmail.addEventListener("submit", (evento) => {
        evento.preventDefault();
        const tokenApi = campoTokenApiEmail.value.trim();

        if (tokenApi.length < 32) {
            mostrarMensagem("O token deve ter pelo menos 32 caracteres.", "erro");
            return;
        }

        tokenApiConfigurado = tokenApi;
        campoTokenApiEmail.value = "";
        modalConfiguracaoEmail.close();
        mostrarMensagem("Token de notificações carregado até esta página ser fechada.", "sucesso");
        window.dispatchEvent(new Event("email:token-configurado"));
    });

    botaoCancelarConfiguracaoEmail?.addEventListener("click", () => modalConfiguracaoEmail.close());
    modalConfiguracaoEmail.addEventListener("click", (evento) => {
        if (evento.target === modalConfiguracaoEmail) modalConfiguracaoEmail.close();
    });
}

export function temTokenApiConfigurado() {
    return Boolean(tokenApiConfigurado);
}

export async function enviarNotificacaoEmail(dadosNotificacao) {
    if (!tokenApiConfigurado) {
        throw new Error("Configure o token da API no menu antes de enviar notificações.");
    }

    const resposta = await fetch(ENDPOINT_NOTIFICACOES_EMAIL, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${tokenApiConfigurado}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dadosNotificacao)
    });

    if (!resposta.ok) {
        const corpoResposta = await resposta.json().catch(() => ({}));
        throw new Error(corpoResposta.erro || `A API de e-mail respondeu com HTTP ${resposta.status}.`);
    }
}
