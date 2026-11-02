import {
    listarTarefas,
    iniciarCadastro,
    iniciarCancelamentoEdicao,
    iniciarAcoesDosCards,
    iniciarStatusMultiplo,
    iniciarSelecionarTodas,
    iniciarAlarmes
} from "./tarefas.js";

import {
    definirTarefas,
    iniciarFiltros
} from "./filtros.js";

import {
    configurarMenu,
    configurarAltoContraste
} from "./ui.js";
import { configurarAgendadorAlarme } from "./alarmes.js";


definirTarefas(
    () => {
        return JSON.parse(
            localStorage.getItem("tarefas")
        ) || [];
    }
);


configurarMenu();

configurarAltoContraste();

const agendadorBackendAlarme = window.todoList?.agendadorAlarme;
if (agendadorBackendAlarme) {
    configurarAgendadorAlarme(agendadorBackendAlarme);
}

iniciarAlarmes();

iniciarCadastro();

iniciarCancelamentoEdicao();

iniciarAcoesDosCards();

iniciarStatusMultiplo();

iniciarSelecionarTodas();

iniciarFiltros();

listarTarefas();