# The container the shell / web arms run model-written commands in (DockerCliExecBackend).
# A JDK image (jar, javap) plus the CLI tools agents reach for when inspecting artifacts on Maven Central.
FROM eclipse-temurin:21-jdk
RUN apt-get update \
 && apt-get install -y --no-install-recommends unzip zip jq git python3 ca-certificates \
 && rm -rf /var/lib/apt/lists/*
# Run as a non-root user; /workspace is the per-run bind mount from the host.
RUN useradd --create-home --uid 1001 agent && mkdir -p /workspace && chown agent /workspace
USER agent
WORKDIR /workspace
