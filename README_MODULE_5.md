# Module 5 - Lab Sample & Investigational Product Tracking

This module covers:

- Sample collection (`collectSample`)
- Lab result recording (`recordLabResult`)
- Investigational product dispense (`dispenseInvestigationalProduct`)
- Inventory status view (`getInventoryStatus`)

## Key files

- `backend/ctds/src/main/java/com/genc/ctds/samplelog/controller/SampleLogController.java`
- `backend/ctds/src/main/java/com/genc/ctds/samplelog/service/SampleLogService.java`
- `backend/ctds/src/main/java/com/genc/ctds/samplelog/model/SampleLog.java`
- `backend/ctds/src/main/java/com/genc/ctds/samplelog/model/InvestigationalProductInventory.java`
- `backend/ctds/src/main/resources/templates/UserForm.html`
- `backend/ctds/src/main/resources/templates/inventory-status.html`

## Endpoints

- `POST /sample/collect`
- `POST /sample/lab-result`
- `POST /sample/ip/dispense`
- `GET /sample/ip/inventory`

## Notes

- Chain-of-custody is tracked using `custodyLog` and `custodyStatus` in `SampleLog`.
- Cold-chain handling is tracked with `coldChainTemperatureC` and `coldChainStatus`.
- IP accountability is tracked through received/dispensed/available quantities.

