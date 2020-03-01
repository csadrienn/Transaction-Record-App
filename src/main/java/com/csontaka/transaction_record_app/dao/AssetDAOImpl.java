package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the methods of
 * {@link com.csontaka.transaction_record_app.dao.AssetRepository} interface.
 *
 * @author Adrienn Csontak
 */
public class AssetDAOImpl implements AssetRepository {

    private final Connection conn;
    private final PreparedStatement findAllAssets;
    private final PreparedStatement findAllProducts;
    private final PreparedStatement findAllEquipment;
    private final PreparedStatement findById;
    private final PreparedStatement findLatest;
    private final PreparedStatement addAsset;
    private final PreparedStatement updateAsset;
    private final PreparedStatement deleteAsset;

    /**
     * Creates a AssetDAOImpl object with a specified connection.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public AssetDAOImpl(Connection conn) throws SQLException {
        this.conn = conn;
        findAllAssets = conn.prepareStatement("SELECT * FROM assets");
        findAllProducts = conn.prepareStatement("SELECT * FROM transaction_records.assets WHERE type = 1");
        findAllEquipment = conn.prepareStatement("SELECT * FROM transaction_records.assets WHERE type = 0");
        findById = conn.prepareStatement("SELECT * FROM transaction_records.assets WHERE id = ?");
        findLatest = conn.prepareStatement("SELECT MAX(id) as id FROM assets");
        addAsset = conn.prepareStatement("INSERT INTO assets (name, feature, type) "
                + "VALUES( ?,  ?,  ?)");
        updateAsset = conn.prepareStatement("UPDATE assets SET "
                + "name = ?, feature = ?, type = ? WHERE id = ?");
        deleteAsset = conn.prepareStatement("DELETE FROM assets WHERE id = ?");
    }

    @Override
    public List<Asset> findAll() throws SQLException {
        List<Asset> assets;
        try (ResultSet allAssets = findAllAssets.executeQuery()) {
            assets = makeList(allAssets);
        }
        return assets;
    }

    @Override
    public List<Asset> findAllProducts() throws SQLException {
        List<Asset> products;
        try (ResultSet allProducts = findAllProducts.executeQuery()) {
            products = makeList(allProducts);
        }
        return products;
    }

    @Override
    public List<Asset> findAllEquipment() throws SQLException {
        List<Asset> equipment;
        try (ResultSet allEquipment = findAllEquipment.executeQuery()) {
            equipment = makeList(allEquipment);
        }
        return equipment;
    }

    @Override
    public Asset findById(Integer id) throws SQLException {
        findById.setInt(1, id);
        Asset asset;
        try (ResultSet assetById = findById.executeQuery()) {
            assetById.beforeFirst();
            assetById.next();
            asset = makeOne(assetById);
        }
        return asset;
    }

    @Override
    public Integer findLatest() throws SQLException {
        Integer id;
        try (ResultSet latestAsset = findLatest.executeQuery()) {
            latestAsset.beforeFirst();
            latestAsset.next();
            id = latestAsset.getInt("id");
        }
        return id;
    }

    @Override
    public void save(Asset asset) throws SQLException {
        if (asset.getId() == null) {
            add(asset);
        } else {
            update(asset);
        }
    }

    private void add(Asset asset) throws SQLException {
        if (asset.getId() == null) {
            addAsset.setString(1, asset.getName());
            addAsset.setString(2, asset.getFeature());
            AssetType type = asset.getType();
            int typeNum = 1;
            if (type == AssetType.EQUIPMENT) {
                typeNum = 0;
            }
            addAsset.setInt(3, typeNum);
            addAsset.executeUpdate();
        }
    }

    private void update(Asset asset) throws SQLException {
        Integer id = asset.getId();
        String name = asset.getName();
        String feature = asset.getFeature();
        AssetType type = asset.getType();
        int typeNum = 1;
        if (type == AssetType.EQUIPMENT) {
            typeNum = 0;
        }
        updateAsset.setString(1, name);
        updateAsset.setString(2, feature);
        updateAsset.setInt(3, typeNum);
        updateAsset.setInt(4, id);
        updateAsset.executeUpdate();
    }

    @Override
    public boolean delete(Asset asset) throws SQLException {
        Integer id = asset.getId();
        if (id != null) {
            this.deleteAsset.setInt(1, id);
            this.deleteAsset.executeUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        findAllProducts.close();
        findAllEquipment.close();
        findById.close();
        findLatest.close();
        addAsset.close();
        updateAsset.close();
        deleteAsset.close();
    }

    private List<Asset> makeList(ResultSet rs) throws
            SQLException {
        List<Asset> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeOne(rs));
        }
        return ret;
    }

    private Asset makeOne(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String assetName = rs.getString("name");
        String feature = rs.getString("feature");
        int typeNum = rs.getInt("type");
        AssetType type = AssetType.EQUIPMENT;
        if (typeNum == 1) {
            type = AssetType.PRODUCT;
        }
        Asset asset = new Asset(assetName, feature, type);
        asset.setId(id);
        return asset;
    }
}
