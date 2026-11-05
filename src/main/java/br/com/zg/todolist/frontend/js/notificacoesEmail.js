const ENDPOINT_NOTIFICACOES_EMAIL =
    "http://127.0.0.1:8081/api/notificacoes/email";

const ENDPOINT_NOTIFICACOES_TELEGRAM =
    "http://127.0.0.1:8082/api/notificacoes/telegram";

// Mantida para compatibilidade com o app.js.
// Não é mais necessário cadastrar token pelo navegador.
export function iniciarConfiguracaoNotificacoesEmail() {
    const botaoConfigurarEmail =
        document.getElementById("botaoConfigurarEmail");

    const modalConfiguracaoEmail =
        document.getElementById("modalConfiguracaoEmail");

    if (botaoConfigurarEmail) {
        botaoConfigurarEmail.hidden = true;
    }

    if (modalConfiguracaoEmail) {
        modalConfiguracaoEmail.hidden = true;

        if (
            typeof modalConfiguracaoEmail.close === "function" &&
            modalConfiguracaoEmail.open
        ) {
            modalConfiguracaoEmail.close();
        }
    }
}

// Compatibilidade com chamadas antigas.
export function temTokenApiConfigurado() {
    return true;
}

// Envio de e-mail.
export async function enviarNotificacaoEmail(dadosNotificacao) {
    await enviarNotificacao(
        ENDPOINT_NOTIFICACOES_EMAIL,
        dadosNotificacao
    );
}

// Envio pelo Telegram.
export async function enviarNotificacaoTelegram(dadosNotificacao) {
    await enviarNotificacao(
        ENDPOINT_NOTIFICACOES_TELEGRAM,
        dadosNotificacao
    );
}

// Comunicação com as APIs Java.
async function enviarNotificacao(endpoint, dadosNotificacao) {

    const resposta = await fetch(endpoint, {
        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(dadosNotificacao)
    });

    if (!resposta.ok) {
        const corpoResposta = await resposta.json().catch(() => ({}));

        throw new Error(
            corpoResposta.erro ||
            `A API respondeu com HTTP ${resposta.status}.`
        );
    }
}