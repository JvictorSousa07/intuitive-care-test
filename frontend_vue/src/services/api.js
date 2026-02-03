const BASE_URL = 'http://localhost:8000/api';

export const api = {
    async listarOperadoras(page = 1, limit = 10, search = '') {
        try {
            const response = await fetch(`${BASE_URL}/operadoras?page=${page}&limit=${limit}&search=${search}`);
            return await response.json();
        } catch (error) {
            console.error("Erro na API:", error);
            return { data: [], meta: { pages: 0 } };
        }
    },
    async getDetalhes(cnpj) {
        const response = await fetch(`${BASE_URL}/operadoras/${cnpj}`);
        return await response.json();
    },
    async getDespesas(cnpj) {
        const response = await fetch(`${BASE_URL}/operadoras/${cnpj}/despesas`);
        return await response.json();
    },
    async getEstatisticas() {
        const response = await fetch(`${BASE_URL}/estatisticas`);
        return await response.json();
    }
};