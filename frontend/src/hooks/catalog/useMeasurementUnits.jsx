import { useCallback, useEffect, useState } from "react";
import CatalogService from "../../services/catalog/CatalogService";

/**
 * Hook to fetch measurement units from the catalog service.
 * Units can be filtered by type (WEIGHT, VOLUME, UNIT, OTHER)
 * 
 * @param {string|null} type - Optional unit type to filter by
 * @returns {Object} - { units, loading, error }
 *   - units: Array of measurement units
 *   - loading: Boolean indicating if the fetch is in progress
 *   - error: Error message if the fetch failed, otherwise null
 */
export default function useMeasurementUnits(type = null) {
    const [units, setUnits] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchUnits = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const result = await CatalogService.listUnits(type);

            if (result.success) {
                setUnits(result.data);
            } else {
                throw new Error(result.error?.message || "Failed to fetch units");
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [type]);

    useEffect(() => {
        fetchUnits();
    }, [fetchUnits]);

    return { units, loading, error };

}