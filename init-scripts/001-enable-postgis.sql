-- PostGIS 확장 활성화
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

SELECT 'PostGIS 확장 설치 완료: ' || PostGIS_Full_Version() as status;