package backend

import (
	"cloud.google.com/go/firestore"
	"context"
	"google.golang.org/api/option"
)

type FirebaseBackend struct {
	client *firestore.Client
}

func NewFirebaseBackend(projectID string, auth string) (*FirebaseBackend, error) {
	ctx := context.Background()
	client, err := firestore.NewClient(ctx, projectID, option.WithCredentialsFile(auth))
	if err != nil {
		return nil, err
	}

	return &FirebaseBackend{client: client}, nil
}

func (f *FirebaseBackend) GetLicense(licenseKey string) (License, error) {
	return f.GetLicenseWithContext(licenseKey, context.Background())
}

func (f *FirebaseBackend) GetLicenseWithContext(licenseKey string, ctx context.Context) (License, error) {
	doc, err := f.client.Collection("licenses").Doc(licenseKey).Get(ctx)
	if err != nil {
		// handle not found error

		return License{}, err
	}

	var license License
	if err := doc.DataTo(&license); err != nil {
		return License{}, err
	}
	license.LicenseKey = licenseKey

	return license, nil
}
