# Guava Range Parser - Makefile
# ==============================
# Common commands for building, testing, and running the project

.PHONY: help build clean test coverage run-examples install \
        format format-check security pitest errorprone sortpom sortpom-check \
        version-get version-set release release-check

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
	@echo "  Release Management:"
	@echo "    make version-get    - Show current project version"
	@echo "    make version-set VERSION=x.y.z - Set new version in all POMs"
	@echo "    make release VERSION=x.y.z     - Complete release: set version, commit, tag, push"
	@echo "    make release-check  - Validate project is ready for release"
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

# =============================================================================
# Release Management
# =============================================================================

version-get:
	@mvn help:evaluate -Dexpression=project.version -q -DforceStdout

version-set:
ifndef VERSION
	$(error VERSION is not set. Usage: make version-set VERSION=x.y.z)
endif
	@echo "Setting version to $(VERSION)..."
	@mvn versions:set -DnewVersion=$(VERSION) -DgenerateBackupPoms=false -q
	@echo "Version updated successfully to $(VERSION)"
	@echo ""
	@echo "Next steps:"
	@echo "  1. Review changes: git diff pom.xml */pom.xml"
	@echo "  2. Commit: git add pom.xml */pom.xml && git commit -m 'chore: bump version to $(VERSION)'"
	@echo "  3. Tag: git tag v$(VERSION)"
	@echo "  4. Push: git push origin main v$(VERSION)"
	@echo ""
	@echo "Or run: make release VERSION=$(VERSION)"

release-check:
	@echo "Checking if project is ready for release..."
	@echo ""
	@# Check for uncommitted changes
	@if [ -n "$$(git status --porcelain)" ]; then \
		echo "❌ Error: Working directory has uncommitted changes"; \
		git status --short; \
		exit 1; \
	fi
	@echo "✓ Working directory is clean"
	@# Check we're on main branch
	@if [ "$$(git branch --show-current)" != "main" ]; then \
		echo "❌ Error: Not on main branch (currently on $$(git branch --show-current))"; \
		exit 1; \
	fi
	@echo "✓ On main branch"
	@# Check we're up to date with remote
	@git fetch origin main --quiet
	@if [ "$$(git rev-parse HEAD)" != "$$(git rev-parse origin/main)" ]; then \
		echo "❌ Error: Local main is not up to date with origin/main"; \
		echo "   Run: git pull"; \
		exit 1; \
	fi
	@echo "✓ Up to date with origin/main"
	@# Run tests
	@echo "Running tests..."
	@mvn clean verify -q
	@echo "✓ All tests passed"
	@echo ""
	@echo "✅ Project is ready for release!"

release: release-check
ifndef VERSION
	$(error VERSION is not set. Usage: make release VERSION=x.y.z)
endif
	@echo ""
	@echo "🚀 Starting release process for version $(VERSION)..."
	@echo ""
	@# Set version
	@echo "1/5 Setting version to $(VERSION)..."
	@mvn versions:set -DnewVersion=$(VERSION) -DgenerateBackupPoms=false -q
	@echo "    ✓ Version updated in all POMs"
	@# Stage changes
	@echo "2/5 Staging changes..."
	@git add pom.xml */pom.xml
	@echo "    ✓ POM files staged"
	@# Commit
	@echo "3/5 Creating commit..."
	@git commit -m "chore: bump version to $(VERSION)" --quiet
	@echo "    ✓ Commit created"
	@# Create tag
	@echo "4/5 Creating tag v$(VERSION)..."
	@if git rev-parse v$(VERSION) >/dev/null 2>&1; then \
		echo "    ⚠️  Tag v$(VERSION) already exists locally, skipping..."; \
	else \
		git tag v$(VERSION); \
		echo "    ✓ Tag v$(VERSION) created"; \
	fi
	@# Push to remote
	@echo "5/5 Pushing to remote..."
	@git push origin main v$(VERSION)
	@echo "    ✓ Pushed to origin"
	@echo ""
	@echo "✅ Release $(VERSION) completed successfully!"
	@echo ""
	@echo "GitHub Release will be created automatically by CI/CD pipeline."
	@echo "Check: https://github.com/neewrobert/guava-range-parser/releases"
