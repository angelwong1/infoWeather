package com.angelwong.infoweather.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    // Migración inicial de la base de datos (1 -> 2)
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Crear tabla temporal para locations
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS locations_temp (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    country TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    timestamp INTEGER NOT NULL,
                    isFavorite INTEGER NOT NULL DEFAULT 0
                )
            """)

            // Copiar datos existentes
            database.execSQL("""
                INSERT INTO locations_temp (id, name, country, latitude, longitude, timestamp)
                SELECT id, name, country, latitude, longitude, timestamp
                FROM locations
            """)

            // Eliminar tabla antigua y renombrar la nueva
            database.execSQL("DROP TABLE locations")
            database.execSQL("ALTER TABLE locations_temp RENAME TO locations")

            // Crear índices necesarios
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_locations_coords ON locations(latitude, longitude)"
            )
        }
    }

    // Lista de todas las migraciones disponibles
    val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
}