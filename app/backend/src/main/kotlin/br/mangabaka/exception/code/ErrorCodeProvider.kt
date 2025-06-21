package br.mangabaka.exception.code

interface ErrorCodeProvider {
    fun handle(valor: String): String
}