/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class DefaultPartition implements Partition {
    public static final String PROP_PARTITIONNAME = "partitionName";
    public static final String PROP_PARTITIONINDEXMAP = "partitionIndexMap";
    public static final String PROP_PARTITIONLABELER = "partitionLabeler";
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String partitionName;
    private PartitionIndexMap partitionIndexMap;
    private PartitionLabeler partitionLabeler;

    public DefaultPartition() {
    }

    public DefaultPartition(String partitionName, PartitionIndexMap partitionIndexMap, PartitionLabeler partitionLabeler) {
        this.partitionName = partitionName;
        this.partitionIndexMap = partitionIndexMap;
        this.partitionLabeler = partitionLabeler;
    }

    /**
     * Get the value of partitionName
     *
     * @return the value of partitionName
     */
    @Override
    public String getPartitionName() {
        return partitionName;
    }

    /**
     * Set the value of partitionName
     *
     * @param partitionName new value of partitionName
     */
    public void setPartitionName(String partitionName) {
        String oldPartitionName = this.partitionName;
        this.partitionName = partitionName;
        propertyChangeSupport.firePropertyChange(PROP_PARTITIONNAME, oldPartitionName, partitionName);
    }


    /**
     * Get the value of partitionIndexMap
     *
     * @return the value of partitionIndexMap
     */
    @Override
    public PartitionIndexMap getPartitionIndexMap() {
        return partitionIndexMap;
    }

    /**
     * Set the value of partitionIndexMap
     *
     * @param partitionIndexMap new value of partitionIndexMap
     */
    public void setPartitionIndexMap(PartitionIndexMap partitionIndexMap) {
        PartitionIndexMap oldPartitionIndexMap = this.partitionIndexMap;
        this.partitionIndexMap = partitionIndexMap;
        propertyChangeSupport.firePropertyChange(PROP_PARTITIONINDEXMAP, oldPartitionIndexMap, partitionIndexMap);
    }


    /**
     * Get the value of partitionLabeler
     *
     * @return the value of partitionLabeler
     */
    @Override
    public PartitionLabeler getPartitionLabeler() {
        return partitionLabeler;
    }

    /**
     * Set the value of partitionLabeler
     *
     * @param partitionLabeler new value of partitionLabeler
     */
    public void setPartitionLabeler(PartitionLabeler partitionLabeler) {
        PartitionLabeler oldPartitionLabeler = this.partitionLabeler;
        this.partitionLabeler = partitionLabeler;
        propertyChangeSupport.firePropertyChange(PROP_PARTITIONLABELER, oldPartitionLabeler, partitionLabeler);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
