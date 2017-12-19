export interface Error {
    error: {
        message: string,
        otherMessage: string,
        error_id: number
    }
}

export let ErrorsList = {
    TIPO_EVENTO_INVALIDO: 1,
    EVENTO_YA_VALIDADO: 2,
    SIN_PERMISOS: 3,
    EVENTO_CAMPOS_INVALIDOS: 4,
    FLICKR_USERNAME_INVALIDO: 5,

    CATEGORIA_NO_EXISTE: 10,
    USUARIO_NO_EXISTE: 11,
    EVENTO_NO_EXISTE: 12,

    NO_AUTENTICADO: 20,
    EXPIRADO: 21,

    OTRO_ERROR: 1000
};