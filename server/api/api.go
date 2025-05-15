package main

import (
	"encoding/json"
	"github.com/go-chi/chi/v5"
	"golang.org/x/time/rate"
	"isxander.dev/xander-drm/backend"
	"net/http"
	"os"
	"path"
	"time"
)

var limiter = NewIPRateLimiter(rate.Every(time.Second*10), 1)

func main() {
	configPath := os.Getenv("XDRM_PATH")
	if configPath == "" {
		configPath = "/var/lib/xander-drm"
	}

	// get bind address from environment variables...
	bind := os.Getenv("XDRM_BIND")
	if bind == "" {
		bind = ":8080"
	}

	// setup firestore client
	projectID := os.Getenv("XDRM_FB_PROJECT_ID")
	authPath := path.Join(configPath, "firebase.json")
	b, err := backend.NewFirebaseBackend(projectID, authPath)
	if err != nil {
		panic(err)
	}

	serve(b, bind)
}

func serve(b backend.Backend, bind string) {
	r := chi.NewRouter()

	r.Use(rateLimiterMiddleware)

	r.Get("/v1/license/{licenseKey}", getLicense(b))

	err := http.ListenAndServe(bind, r)
	if err != nil {
		return
	}
}

func getLicense(b backend.Backend) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		licenseKey := chi.URLParam(r, "licenseKey")

		// Call the backend to get the license
		license, err := b.GetLicense(licenseKey)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		// Write the license to the response
		if err := json.NewEncoder(w).Encode(license); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json")
	}
}

func rateLimiterMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		l := limiter.GetLimiter(r.RemoteAddr)
		if !l.Allow() {
			http.Error(w, "Too many requests", http.StatusTooManyRequests)
			return
		}

		next.ServeHTTP(w, r)
	})
}
