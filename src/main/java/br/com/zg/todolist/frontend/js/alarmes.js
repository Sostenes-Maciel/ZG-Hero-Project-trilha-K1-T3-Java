const agendamentos = new Map();
let agendador = sincronizarAlarmesLocais;

function notificarLocalmente(tarefa) {
    window.dispatchEvent(new CustomEvent("tarefas:alarme-disparado", {
        detail: { tarefa }
    }));
    alert(`ALARME DE TAREFA\n\nTarefa: ${tarefa.nome}\nDescrição: ${tarefa.descricao}`);
}

export function configurarAgendadorAlarme(novoAgendador) {
    if (typeof novoAgendador !== "function") {
        throw new TypeError("O agendador de alarmes deve ser uma função.");
    }

    agendador = novoAgendador;
}

export async function sincronizarAlarmes(tarefas) {
    await agendador(tarefas);
}

function sincronizarAlarmesLocais(tarefas) {
    agendamentos.forEach((timeout) => clearTimeout(timeout));
    agendamentos.clear();

    tarefas.forEach((tarefa) => {
        if (tarefa.alarme) agendarAlarme(tarefa);
    });
}

function agendarAlarme(tarefa) {
    const [hora, minuto] = tarefa.alarme.split(":").map(Number);
    const agora = new Date();
    const horarioAlarme = new Date(agora);

    horarioAlarme.setHours(hora, minuto, 0, 0);
    if (horarioAlarme <= agora) {
        horarioAlarme.setDate(horarioAlarme.getDate() + 1);
    }

    const chave = String(tarefa.id);
    const timeout = setTimeout(() => {
        agendamentos.delete(chave);
        notificarLocalmente(tarefa);
    }, horarioAlarme.getTime() - agora.getTime());

    agendamentos.set(chave, timeout);
}
