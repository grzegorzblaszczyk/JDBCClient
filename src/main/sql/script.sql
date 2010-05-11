-- liczba kolumn:2
SELECT w.product_id product_id, p.image1_path image1_path FROM dcs_product_binary_watch w, dcs_product p WHERE w.product_id=p.product_id
DELETE FROM dcs_product_binary_watch
