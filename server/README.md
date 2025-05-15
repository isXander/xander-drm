# Xander DRM Server

This is the backend server for the Xander DRM system. It provides license validation services via a REST API.

## Docker Setup

The server can be run as a Docker container. The Docker image is automatically built and published to GitHub Container Registry (ghcr.io) when changes are pushed to the main branch or when a new version tag is created.

### Using the Pre-built Image

To use the pre-built image from GitHub Container Registry:

```bash
docker pull ghcr.io/isXander/xander-drm/server:latest
```

### Running the Container

To run the container:

```bash
docker run -p 8080:8080 \
  -v /path/to/your/firebase.json:/var/lib/xander-drm/firebase.json \
  -e XDRM_FB_PROJECT_ID=your-firebase-project-id \
  ghcr.io/isXander/xander-drm/server:latest
```

### Environment Variables

The following environment variables can be used to configure the server:

- `XDRM_PATH`: Path to configuration files (default: /var/lib/xander-drm)
- `XDRM_BIND`: Server bind address (default: :8080)
- `XDRM_FB_PROJECT_ID`: Firebase project ID (required)

### Building Locally

To build the Docker image locally:

```bash
cd server
docker build -t xander-drm-server .
```

To run the locally built image:

```bash
docker run -p 8080:8080 \
  -v /path/to/your/firebase.json:/var/lib/xander-drm/firebase.json \
  -e XDRM_FB_PROJECT_ID=your-firebase-project-id \
  xander-drm-server
```

## API Endpoints

### Get License

```
GET /v1/license/{licenseKey}
```

Returns license information for the specified license key.

#### Response

```json
{
  "license_key": "your-license-key",
  "permit": true,
  "reason": ""
}
```

## Development

### Prerequisites

- Go 1.24 or higher
- Firebase project with Firestore database

### Running Locally

1. Set up your Firebase project and download the service account key file as `firebase.json`
2. Place the `firebase.json` file in the `run` directory
3. Set the environment variables:
   ```bash
   export XDRM_FB_PROJECT_ID=your-firebase-project-id
   ```
4. Run the server:
   ```bash
   go run api/api.go
   ```

The server will be available at http://localhost:8080.