export function prioridadeValida(prioridade) {
    const regex = /^[1-5]$/;

    return regex.test(prioridade);
}


export function dataFormatoValido(data) {
    const regex = /^\d{4}-\d{2}-\d{2}$/;

    return regex.test(data);
}


export function dataNaoAnteriorHoje(data) {

    if (!dataFormatoValido(data)) {
        return false;
    }

    const [ano, mes, dia] = data.split("-").map(Number);

    const dataInformada = new Date(ano, mes - 1, dia);

    const hoje = new Date();

    hoje.setHours(0, 0, 0, 0);

    return dataInformada >= hoje;
}