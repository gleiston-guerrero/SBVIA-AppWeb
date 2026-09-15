-include .env
.PHONY: all verify backend-verify frontend-build build up down bench audit pdf clean

MAVEN_IMAGE ?= maven:3.9.11-eclipse-temurin-21-alpine
NODE_IMAGE ?= node:20-alpine

all: verify build up pdf

verify: backend-verify frontend-build

backend-verify:
	docker run --rm -v "$(CURDIR)/backend:/app" -w /app $(MAVEN_IMAGE) mvn -B clean verify

frontend-build:
	docker run --rm -v "$(CURDIR)/frontend:/app" -w /app $(NODE_IMAGE) sh -c "npm ci && npm run build -- --configuration production"

build:
	docker compose build

up:
	docker compose up -d --wait

down:
	docker compose down

bench:
	@echo "Ejecutando pruebas de carga k6..."
	k6 run -e K6_USERNAME='$(K6_USERNAME)' -e K6_PASSWORD='$(K6_PASSWORD)' scripts/k6/load-test.js

audit:
	@echo "Ejecutando auditoria Lighthouse para ADMINISTRADOR..."
	npx --yes cross-env CHROME_PATH="C:\Program Files\Google\Chrome\Application\chrome.exe" TEST_USER_EMAIL=admin@sbvia.com TEST_USER_PASSWORD='$(TEST_ADMIN_PASSWORD)' LHCI_OUTPUT_DIR=docs/mediciones/lighthouse/administrador npx --yes @lhci/cli@0.15.1 autorun --config=lighthouserc-auth.js
	@echo "Ejecutando auditoria Lighthouse para INSTRUCTOR..."
	npx --yes cross-env CHROME_PATH="C:\Program Files\Google\Chrome\Application\chrome.exe" TEST_USER_EMAIL=instructor@sbvia.com TEST_USER_PASSWORD='$(TEST_INSTRUCTOR_PASSWORD)' LHCI_OUTPUT_DIR=docs/mediciones/lighthouse/instructor npx --yes @lhci/cli@0.15.1 autorun --config=lighthouserc-auth.js
	@echo "Ejecutando auditoria Lighthouse para PARTICIPANTE..."
	npx --yes cross-env CHROME_PATH="C:\Program Files\Google\Chrome\Application\chrome.exe" TEST_USER_EMAIL=participante@sbvia.com TEST_USER_PASSWORD='$(TEST_PARTICIPANT_PASSWORD)' LHCI_OUTPUT_DIR=docs/mediciones/lighthouse/participante npx --yes @lhci/cli@0.15.1 autorun --config=lighthouserc-auth.js

clean:
	docker compose down --volumes --remove-orphans

pdf:
	@echo "Compilando informe LaTeX (3 pasadas)..."
	cd docs && pdflatex -interaction=nonstopmode informe-final.tex
	cd docs && bibtex informe-final
	cd docs && pdflatex -interaction=nonstopmode informe-final.tex
	cd docs && pdflatex -interaction=nonstopmode informe-final.tex
	@echo "PDF generado: docs/informe-final.pdf"
