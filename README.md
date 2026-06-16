# Ostermiller Java Utils

Open source (GPL) Java utilities maintained by Stephen Ostermiller with help from many contributors.

**Visit the official website: https://utils.ostermiller.org/** for pre-compiled downloads and full documentation.

## Building

### Prerequisites

- **Java 8+**: Required for compilation and running tests
- **Maven 3.x**: Build system and dependency management
- **GNU Make**: Orchestrates the Maven build with idempotent file-based targets
- **Perl**: Required for generating site documentation files
- **pre-commit**: Git hooks framework for code quality checks (optional but recommended)

### Installing Dependencies

#### On Ubuntu/Debian:
```bash
sudo apt-get install default-jdk maven make perl pipx
pipx install pre-commit
```

#### On macOS (with Homebrew):
```bash
brew install openjdk maven pre-commit
# Make and Perl should already be installed
```

#### On Windows:
- Download and install [OpenJDK](https://jdk.java.net/)
- Download and install [Maven](https://maven.apache.org/download.cgi)
- [GNU Make for Windows](http://gnuwin32.sourceforge.net/packages/make.htm)
- [Perl for Windows](https://strawberryperl.com/)
- [pre-commit](https://pre-commit.com/#install) (requires Python)

### Build Commands

All builds are orchestrated through Make, which uses Maven internally:

```bash
# Build everything (compile, test, package, site)
make

# Clean build artifacts
make clean
```

The build is idempotent, running it a second time will only rebuid if there are changes.

### Build Output

After running `make`, you'll find:

- **Archive files** in `target/`:
  - `.jar` file with compiled classes
  - `-sources.jar` file with Java source code
  - `-bin.tar.bz2`, `-bin.tar.gz`, `-bin.zip` containing the jar files
  - `-project.tar.bz1`, `-project.tar.gz`, `-project.zip` containing the full source code.
- **API Documentation** in `target/site/doc/`
- **Website** in `target/site/`
- **Test Results** in `target/surefire-reports/`

### Version Management

The project version is stored in `VERSION.txt` and is automatically substituted into all build artifacts:

```bash
cat VERSION.txt  # View current version
```

## License

GNU General Public License, version 2. See [LICENSE.txt](LICENSE.txt) for details.
