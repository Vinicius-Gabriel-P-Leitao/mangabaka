package br.mangabaka.exception.throwable.http

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.exception.throwable.base.AppException

// @formatter:off
class AssetDownloadException(message: String, errorCode: ErrorCodeProvider, cause: Throwable? = null) : AppException(message, errorCode, cause)