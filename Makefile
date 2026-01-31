# Guava Range Parser - Makefile
# ==============================
# Common commands for building, testing, and running the project

.PHONY: help build clean test coverage run-examples install \
        format format-check security pitest errorprone sortpom sortpom-check

# Default target
help:
	@echo "Guava Range Parser - Available targets:"
	@echo ""
	@echo "  Build & Install:"
	@echo "    make build          - Compile the project"
	@echo "    make install        - Install to local Maven repository"
	@echo "    make clean          - Clean build artifacts"
	@echo ""
	@echo "  Testing:"
	@echo "    make test           - Run unit tests"
	@echo "    make coverage       - Run tests with JaCoCo coverage report"
	@echo "    make pitest         - Run mutation testing with Pitest"
	@echo ""
	@echo "  Examples:"
	@echo "    make run-examples   - Run the examples Spring Boot application"
	@echo ""
	@echo "  Code Quality:"
	@echo "    make format         - Format code with google-java-format"
	@echo "    make format-check   - Check code formatting (CI)"
	@echo "    make errorprone     - Run Error Prone static analysis"
	@echo "    make sortpom        - Sort pom.xml files"
	@echo "    make sortpom-check  - Verify pom.xml sorting (CI)"
	@echo ""
	@echo "  Security:"
	@echo "    make security       - Run OWASP Dependency-Check (requires NVD_API_KEY)"
	@echo ""

# =============================================================================
# Build & Install
# =============================================================================

build:
	mvn compile -q

install:
	mvn install -q

clean:
	mvn clean -q

# =============================================================================
# Testing
# =============================================================================

test:
	mvn test

coverage:
	mvn test -Pcoverage
	@echo ""
	@echo "Coverage reports generated:"
	@echo "  - guava-range-parser-core/target/site/jacoco/index.html"
	@echo "  - guava-range-parser-jackson/target/site/jacoco/index.html"
	@echo "  - guava-range-parser-spring/target/site/jacoco/index.html"

pitest:
	mvn test -Ppitest
	@echo ""
	@echo "Mutation testing reports generated in target/pit-reports/"

# =============================================================================
# Examples
# =============================================================================

run-examples:
	cd guava-range-parser-examples && mvn spring-boot:run

# =============================================================================
# Code Quality
# =============================================================================

format:
	mvn com.spotify.fmt:fmt-maven-plugin:format

format-check:
	mvn com.spotify.fmt:fmt-maven-plugin:check

errorprone:
	mvn compile -Perrorprone

sortpom:
	mvn com.github.ekryd.sortpom:sortpom-maven-plugin:sort

sortpom-check:
	mvn validate -Psortpom

# =============================================================================
# Security
# =============================================================================

security:
ifndef NVD_API_KEY
	$(error NVD_API_KEY is not set. Get one at https://nvd.nist.gov/developers/request-an-api-key)
endif
	mvn verify -Pdev,security -DskipTests "-DnvdApiKey=$(NVD_API_KEY)"
	@echo ""
	@echo "Security report generated in target/dependency-check/"
