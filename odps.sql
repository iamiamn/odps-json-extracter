#新建表格
CREATE TABLE IF NOT EXISTS train11 AS SELECT * FROM odps_tc_257100_f673506e024.adl_tianchi_renji_training_fdt;
CREATE TABLE IF NOT EXISTS test1_1 AS SELECT * FROM odps_tc_257100_f673506e024.adl_tianchi_renji_test_fdt1
#按照https://help.aliyun.com/document_detail/27811.html?spm=5176.doc27812.6.553.Sq6vNF 的指导，配置本地maven项目，在本地编译好包含parseJson类的jar包
#后，在数加平台\资源管理，上传java文件，然后新建sql任务，输入下面语句
DROP FUNCTION IF EXISTS parseJson;
CREATE FUNCTION parseJson1 AS jsonProcess.parseJson USING train1_json_extract.jar;
#从json字符串提取特征
CREATE TABLE IF NOT EXISTS train1_jsoned AS SELECT parseJson1(a1, "EF") AS elemFocus, a2 AS jsLoadTime, parseJson1(a3, "MC") AS mouseClick, parseJson1(a4, "MM") AS mouseMove,
parseJson1(a5, "MS") AS mouseSample, parseJson1(a7, "KB"), id, label AS keyBoard FROM train11;

#特征二次转换：第一次提取的字符串结果，结果中包含多个统计数据，数据间用空格隔开，因此利用split_part分割
#split_part函数解释：odps的sql内建函数列表参考网址 http://www.aiwanba.com/plugin/odps-doc/prddoc/odps_sql/odps_sql_func.html
CREATE TABLE IF NOT EXISTS train1_extracted AS SELECT id, label, jsLoadTime, split_part(elemFocus, " ", 1) AS ef_total_count, split_part(elemFocus, " ", 2) AS ef_type1_rate, split_part(elemFocus, " ", 3) AS ef_time_sd, split_part(elemFocus, " ", 4) AS ef_time_range, split_part(mouseClick, " ", 1) AS mc_total_count, split_part(mouseClick, " ", 2) AS ms_time_sep, split_part(mouseClick, " ", 3) AS mc_time_range, split_part(mouseClick, " ", 4) AS mc_x_sd, split_part(mouseClick, " ", 5) AS mc_y_sd, split_part(mouseClick, " ", 6) AS mc_x_range, split_part(mouseClick, " ", 7) AS mc_y_range, split_part(mouseMove, " ", 1) AS mm_time_sep, split_part(mouseMove, " ", 2) AS mm_time_range, split_part(mouseMove, " ", 3) AS mm_x_sd, split_part(mouseMove, " ", 4) AS mm_y_sd, split_part(mouseMove, " ", 5) AS mm_x_range, split_part(mouseMove, " ", 6) AS mm_y_range, split_part(mouseMove, " ", 7) AS mm_total_count, split_part(mouseSample, " ", 1) AS ms_x_sd, split_part(mouseSample, " ", 2) AS ms_y_sd, split_part(mouseSample, " ",3) AS ms_x_range, split_part(mouseSample, " ", 4) AS ms_y_range, split_part(keyBoard, " ", 1) AS kb_total_count, split_part(keyBoard, " ", 2) AS kb_time_sd, split_part(keyBoard, " ", 3) AS kb_time_range FROM train1_jsoned;

