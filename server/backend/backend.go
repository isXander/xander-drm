package backend

import "context"

type Backend interface {
	GetLicense(licenseKey string) (License, error)
	GetLicenseWithContext(licenseKey string, ctx context.Context) (License, error)
}

type License struct {
	LicenseKey string `json:"license_key"`
	Permit     bool   `json:"permit"`
	Reason     string `json:"reason"`
}

type LicenseNotFound struct {
	LicenseKey string
}

func (e *LicenseNotFound) Error() string {
	return "license not found: " + e.LicenseKey
}
