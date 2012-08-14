/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table;

import java.util.List;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class ListBinLabeler implements BinLabeler {

    List labels;

    ListBinLabeler(List labels) {
        this.labels = labels;
    }

    @Override
    public String getLabel(int binIndex) {
        if (labels != null && binIndex >= 0 && binIndex < labels.size()) {
            Object obj = labels.get(binIndex);
            if (obj != null) {
                return obj.toString();
            }
        }
        return "";
    }
}

