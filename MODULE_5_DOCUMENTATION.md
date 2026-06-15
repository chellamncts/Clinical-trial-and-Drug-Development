# Module 5 Documentation

## Purpose

Tracks lab specimens and investigational product accountability for clinical trial operations.

## Controller

`SampleLogController`

- `collectSample()` -> `POST /sample/collect`
- `recordLabResult()` -> `POST /sample/lab-result`
- `dispenseInvestigationalProduct()` -> `POST /sample/ip/dispense`
- `getInventoryStatus()` -> `GET /sample/ip/inventory`

## Service

`SampleLogService`

- Initializes sample status and chain-of-custody at collection time.
- Records lab result and updates sample state to analyzed.
- Dispenses investigational product with quantity checks and cold-chain classification.
- Exposes current inventory records.

## Models

### `SampleLog`

- `sampleId`
- `subjectId`
- `sampleType`
- `collectionDate`
- `labResult`
- `sampleStatus`
- `custodyLog`
- `coldChainTemperatureC`
- `custodyStatus`

### `InvestigationalProductInventory`

- `inventoryId`
- `productName`
- `batchNumber`
- `quantityReceived`
- `quantityDispensed`
- `quantityAvailable`
- `storageTemperatureC`
- `coldChainStatus`

