## 🗄️ Structure de la base de données

Voici les principales tables utilisées dans le système central :

### 📍 `SalesPoint` – Points de vente  
Contient les informations de base sur chaque point de vente.

```sql
CREATE TABLE SalesPoint (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Dish (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    salesPointId BIGINT NOT NULL,
    FOREIGN KEY (salesPointId) REFERENCES SalesPoint(id)
);
CREATE TABLE SalesSummary (
    id BIGSERIAL PRIMARY KEY,
    dishId BIGINT NOT NULL,
    salesPointId BIGINT NOT NULL,
    quantitySold INTEGER NOT NULL DEFAULT 0,
    totalAmount DECIMAL(10,2) NOT NULL DEFAULT 0,
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dishId) REFERENCES Dish(id),
    FOREIGN KEY (salesPointId) REFERENCES SalesPoint(id),
    UNIQUE (dishId, salesPointId)
);
CREATE TABLE ProcessingTimeSummary (
    id BIGSERIAL PRIMARY KEY,
    dishId BIGINT NOT NULL,
    salesPointId BIGINT NOT NULL,
    averageProcessingTime DECIMAL(10,2) NOT NULL,
    minimumProcessingTime DECIMAL(10,2) NOT NULL,
    maximumProcessingTime DECIMAL(10,2) NOT NULL,
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dishId) REFERENCES Dish(id),
    FOREIGN KEY (salesPointId) REFERENCES SalesPoint(id),
    UNIQUE (dishId, salesPointId)
);
CREATE TABLE SynchronizationLog (
    id BIGSERIAL PRIMARY KEY,
    synchronizedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
