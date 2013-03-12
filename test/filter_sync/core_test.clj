(ns filter-sync.core-test
  (:use clojure.test
        filter-sync.core))

(deftest testZipSeq
	(testing "The doc.zip contains: a.txt and b.gif."
		(is (= ["b.gif" "a.txt"] (zip-seq "sample/simple/doc.zip")))))

(deftest testZipFilter
	(testing "The doc.zip contains one text file: a.txt."
		(is (= ["a.txt"] (zip-filter "sample/simple/doc.zip" [".txt"])))))

(import java.io.File)
(deftest testZipCopy
	(testing "Check target/doc.zip if it's identical."
		(do
			(zip-copy "sample/simple/doc.zip" "target/doc.zip" '("a.txt" "b.gif"))
			(is (= 2 (count (zip-seq "target/doc.zip")))))))

(deftest testZipFilterCopy
	(testing "Check out/doc.zip if it contains only text files."
		(do
			(zip-filter-copy "sample/simple/doc.zip" "target/doc2.zip" [".txt"])
			(is (= 1 (count (zip-seq "target/doc2.zip")))))))

(deftest testZipFilterCopyEmpty
	(testing "Empty zip is not copied."
		(do
			(-> "target/doc3.zip" File. .delete)
			(zip-filter-copy "sample/simple/doc.zip" "target/doc3.zip" [".nofile"])
			(is (not (-> "target/doc3.zip" File. .exists))))))

(deftest testRelativePath
	(testing "base/path, base/path/file, -> file"
		(is (= "/file" 
			(rel-path
				(clojure.java.io/file "base/path")
				(clojure.java.io/file "base/path/file"))))))

(import java.io.File org.apache.commons.io.FileUtils)
(deftest testFolderCopy
	(testing "Check target/simple for two files."
		(do
			(FileUtils/deleteDirectory (File. "target/simple"))
			(folder-copy (File. "sample/simple") (File. "target/simple")
		  	[(File. "sample/simple/a.txt")
		  	(File. "sample/simple/folder")
		  	(File. "sample/simple/folder/b.gif")]
		  	clojure.java.io/copy)
			(is (= 4 (count (file-seq (File. "target/simple"))))))))

(deftest testFolderFilter
	(testing "file-seq with conditional filters"
		(is (= ["sample/simple/a.txt" "sample/simple/folder/a.txt"]
			(map #(.replace (.getPath %) \\ \/) (folder-filter "sample/simple" [".txt"]))))))

(deftest testFolderFilterCopy
	(testing "Folder copy with given filters including zip files."
		(do
			(FileUtils/deleteDirectory (File. "target/simple2"))
			(folder-filter-copy "sample/simple" "target/simple2" [".txt"])
			(is (= 5 (count (file-seq (File. "target/simple2")))))
			(is (= 1 (count (zip-seq "target/simple2/doc.zip")))))))
