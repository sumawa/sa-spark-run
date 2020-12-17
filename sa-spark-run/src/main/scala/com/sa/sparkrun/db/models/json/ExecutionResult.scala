package com.sa.sparkrun.db.models.json

import java.util.UUID

object ExecutionResult {
  type FileName        = String
  type DatasetUuid     = UUID
  type ExecutionResult = Map[FileName, DatasetUuid]
}
