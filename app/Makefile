# SPDX-License-Identifier: BSD-3-Clause
#
# Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
# Licensed under the BSD 3-Clause License.
# See LICENSE file in the project root for full license information.
ENV_DEV=docker/development/.env
ENV_PROD=docker/production/.env
LOCAL_PROPS=local.properties

.PHONY: all dev prod build-backend build-frontend clean generate-env-dev generate-env-prod docker-dev docker-prod

define prop
$(shell grep '^$(1)=' $(LOCAL_PROPS) | cut -d'=' -f2 || echo "undefined")
endef

# ===============================
# ENV
# ===============================
generate-env-dev:
	@echo "Gerando .env de DEV..."
	@echo "APP_LOCALE=$(call prop,DEV_APP_LOCALE)" > $(ENV_DEV)
	@echo "BACKEND_MODE=$(call prop,DEV_BACKEND_MODE)" >> $(ENV_DEV)
	@echo "PG_DB_NAME=$(call prop,DEV_DB_NAME)" >> $(ENV_DEV)
	@echo "PG_PASSWORD=$(call prop,DEV_PASSWORD)" >> $(ENV_DEV)
	@echo "PG_USERNAME=$(call prop,DEV_USERNAME)" >> $(ENV_DEV)
	@echo "PG_JDBC_URL=$(call prop,DEV_JDBC_URL)" >> $(ENV_DEV)
	@echo "✅ .env DEV gerado em $(ENV_DEV)"

generate-env-prod:
	@echo "Gerando .env de PROD..."
	@echo "APP_LOCALE=$(call prop,PROD_APP_LOCALE)" > $(ENV_PROD)
	@echo "BACKEND_MODE=$(call prop,PROD_BACKEND_MODE)" >> $(ENV_PROD)
	@echo "PG_DB_NAME=$(call prop,PROD_DB_NAME)" >> $(ENV_PROD)
	@echo "PG_PASSWORD=$(call prop,PROD_PASSWORD)" >> $(ENV_PROD)
	@echo "PG_USERNAME=$(call prop,PROD_USERNAME)" >> $(ENV_PROD)
	@echo "PG_JDBC_URL=$(call prop,PROD_JDBC_URL)" >> $(ENV_PROD)
	@echo "✅ .env PROD gerado em $(ENV_PROD)"

# ===============================
# FRONTEND
# ===============================
build-frontend:
	cd frontend/mangabaka && npm install && npm run build \

# ===============================
# BACKEND
# ===============================
build-backend: build-frontend
	cp -r frontend/mangabaka/dist backend/src/main/webapp && cd backend && gradle build war

# ===============================
# DOCKER
# ===============================
docker-dev: generate-env-dev build-backend
	docker compose -f docker/development/mangabaka-docker-compose-dev.yml -p mangabaka-dev up -d postgresql-dev
	docker compose -f docker/development/mangabaka-docker-compose-dev.yml -p mangabaka-dev up -d jetty --force-recreate

docker-prod: generate-env-prod
	docker compose -f docker/production/mangabaka-docker-compose-prod.yml -p mangabaka-prod up -d postgresql-prod
	@if [ "$(call prop,PROD_BACKEND_MODE)" = "ALL" ]; then \
		docker compose -f docker/production/mangabaka-docker-compose-prod.yml -p mangabaka-prod up -d mangabaka-all; \
	elif [ "$(call prop,PROD_BACKEND_MODE)" = "API" ]; then \
		docker compose -f docker/production/mangabaka-docker-compose-prod.yml -p mangabaka-prod up -d mangabaka-api; \
	else \
		echo "❌ Valor de PROD_BACKEND_MODE inválido"; \
		exit 1; \
	fi

# ===============================
# ORQUESTRADORES
# ===============================
dev: docker-dev
	@echo "✅ Ambiente de DEV pronto"

prod: docker-prod
	@echo "✅ Ambiente de PRODUÇÃO pronto"

# ===============================
# CLEAN
# ===============================
clean:
	rm -rf backend/build frontend/mangabaka/dist backend/src/main/webapp
	rm -f $(ENV_DEV) $(ENV_PROD)
	@echo "🧹 Build e arquivos .env limpos!"
