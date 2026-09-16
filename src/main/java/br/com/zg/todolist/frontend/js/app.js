import {
    listarTarefas,
    iniciarCadastro,
    iniciarCancelamentoEdicao,
    iniciarAcoesDosCards,
    iniciarStatusMultiplo,
    iniciarSelecionarTodas
} from "./tarefas.js";

import {
    definirTarefas,
    iniciarFiltros
} from "./filtros.js";

import {
    configurarMenu,
    configurarAltoContraste
} from "./ui.js";


definirTarefas(
    () => {
        return JSON.parse(
            localStorage.getItem("tarefas")
        ) || [];
    }
);


configurarMenu();

configurarAltoContraste();

iniciarCadastro();

iniciarCancelamentoEdicao();

iniciarAcoesDosCards();

iniciarStatusMultiplo();

iniciarSelecionarTodas();

iniciarFiltros();

listarTarefas();