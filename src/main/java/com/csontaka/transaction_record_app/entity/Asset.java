package com.csontaka.transaction_record_app.entity;

/**
 * Represents an asset.
 *
 * @author Adrienn Csonták
 */
public class Asset {

    private Integer id;
    private String name;
    private String feature;
    private AssetType type;

    /**
     * Default constructor to create an Asset.
     */
    public Asset() {

    }

    /**
     * Creates an asset with specified name, feature and state of being product
     * or equipment.
     *
     * @param name The asset name.
     * @param feature The asset feature, description.
     * @param type The asset state of being product or equipment.
     */
    public Asset(String name, String feature, AssetType type) {
        this.id = null;
        this.name = name;
        this.feature = feature;
        this.type = type;
    }

    /**
     * Sets the asset's unique id.
     *
     * @param id An Integer containing the asset's unique id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the asset's unique id.
     *
     * @return An Integer representing the asset's unique id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the asset's type.
     *
     * @return An AssetType representing the asset's type.
     */
    public AssetType getType() {
        return type;
    }

    /**
     * Sets the asset's type.
     *
     * @param type An AssetType containing the asset's type.
     */
    public void setType(AssetType type) {
        this.type = type;
    }

    /**
     * Gets the asset’s name.
     *
     * @return A String representing the asset's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the asset’s name.
     *
     * @param name A String containing the asset's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the asset’s feature, description.
     *
     * @return A String representing the asset's feature.
     */
    public String getFeature() {
        return feature;
    }

    /**
     * Sets the asset’s feature, description.
     *
     * @param feature A String containing the asset's feature.
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

    @Override
    public String toString() {
        return id + ", " + name;
    }

}
