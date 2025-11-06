package com.project.finance_webflux.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // evita instanciar
public final class Mensaje {

    public static final String USERNAME_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String USERNAME_EXISTE = "Username ya existe";
    public static final String EMAIL_EXISTE = "Email ya existe";
    public static final String ROL_USER = "USER";
    public static final String LOGIN_PASSWORD_INCORRECTO = "Contraseña incorrecta";
    public static final String LOGIN_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
}
