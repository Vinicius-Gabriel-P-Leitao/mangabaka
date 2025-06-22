package br.mangabaka.exception.code

interface ErrorCodeProvider {
    fun handle(value: String): String
}