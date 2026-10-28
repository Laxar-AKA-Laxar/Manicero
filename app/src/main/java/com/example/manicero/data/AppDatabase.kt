package com.example.manicero.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.manicero.model.Producto
import com.example.manicero.model.Usuario

@Database(entities = [Producto::class, Usuario::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oyl_frutos_secos.db"
                ).fallbackToDestructiveMigration(true).build()
                instancia = nueva
                nueva
            }
        }
    }
}