package com.example.twitter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.MongoException;

@Component
public class MongoTestConnection implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;

    public MongoTestConnection(MongoTemplate mongoTemplate, MongoClient mongoClient) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(String... args) {
        System.out.println("🔍 Verificando conexión con MongoDB...");
        try {
            // Obtener nombre de la base de datos
            MongoDatabase database = mongoClient.getDatabase(mongoTemplate.getDb().getName());
            if (database == null || database.getName().isEmpty()) {
                throw new MongoException("No se pudo conectar a la base de datos");
            }
            System.out.println("✅ Conexión exitosa a MongoDB");
            System.out.println("📂 Base de datos seleccionada: " + database.getName());

            // Verificar colecciones disponibles
            System.out.println("📌 Colecciones en la base de datos:");
            for (String collectionName : database.listCollectionNames()) {
                System.out.println("   - " + collectionName);
            }

            // Probar una consulta simple (contar documentos en una colección específica)
            long count = database.getCollection("usuarios").countDocuments();
            System.out.println("📊 Documentos en la colección 'usuarios': " + count);

        } catch (MongoException e) {
            System.err.println("❌ Error al conectar a MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
