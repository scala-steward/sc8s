# Environment Management & Direnv

- Always check if the current workspace contains a `.envrc` file before executing any shell, build, test, compilation, or run commands.
- If a `.envrc` file is present, always run commands through `direnv exec . <command>` or prefix them by sourcing it (e.g., `direnv allow && direnv exec . sbt compile`).
- Never modify build configuration files (such as `build.sbt` or `plugins.sbt`) to manually parse `.envrc` or inject environment variables unless explicitly instructed to do so.
