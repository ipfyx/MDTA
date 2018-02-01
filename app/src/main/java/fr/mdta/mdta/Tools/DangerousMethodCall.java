package fr.mdta.mdta.Tools;

/**
 * We detect 4 types of dangerous method usage
 * TODO : detect cryptography
 */

public enum DangerousMethodCall {
    REFLECTION,
    SHELL,
    LOAD_CPP_LIBRARY,
    SELINUX
}
