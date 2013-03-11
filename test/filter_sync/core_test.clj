(ns filter-sync.core-test
  (:use clojure.test
        filter-sync.core))

(deftest testZipSeq
	(testing "There are two files in doc.zip."
		(= 2 (count (zip-seq "sample/simple/doc.zip")))))

(deftest testZipSeq2
	(testing "The doc.zip contains: a.txt and b.gif."
		(= ["a.txt" "b.gif"] (zip-seq "sample/simple/doc.zip"))))

(deftest testZipFilter
	(testing "There is one text file in doc.zip."
		(= 1 (count (zip-filter "sample/simple/doc.zip" ".txt")))))

(deftest testZipFilter2
	(testing "The doc.zip contains one text file: a.txt."
		(= ["a.txt"] (zip-filter "sample/simple/doc.zip" ".txt"))))

(import java.io.File)
(deftest testZipCopy
	(testing "Check out/doc.zip if it's identical."
		(do
			(.mkdirs (File. "sample/simple/out"))
			(zip-copy "sample/simple/doc.zip" "sample/simple/out/doc.zip" '("a.txt" "b.gif"))
			(= 2 (count (zip-seq "sample/simple/out/doc.zip"))))))

(deftest testZipFilterCopy
	(testing "Check out/doc.zip if it contains only text files."
		(do
			(zip-filter-copy "sample/simple/doc.zip" "sample/simple/out/doc2.zip" ".txt")
			(= 1 (count (zip-seq "sample/simple/out/doc2.zip"))))))

(deftest testRelativePath
	(testing "base/path, base/path/file, -> file"
		(= "file" 
			(rel-path
				(clojure.java.io/file "base/path")
				(clojure.java.io/file "base/path/file")))))

(import java.io.File org.apache.commons.io.FileUtils)
(deftest testFolderCopy
	(testing "Check target/simple for two files."
		(do
			(FileUtils/deleteDirectory (File. "target/simple"))
			(folder-copy (File. "sample/simple") (File. "target/simple")
		  	[(File. "sample/simple/a.txt")
		  	(File. "sample/simple/folder")
		  	(File. "sample/simple/folder/b.gif")])
			(= 2 (count (file-seq (File. "target/simple")))))))

(deftest testFolderFilter
	(testing "file-seq with conditional filters"
		(= 1 (count (folder-filter "sample/simple" ".txt")))))

(deftest testFolderFilter2
	(testing "file-seq with conditional filters"
		(= ["a.txt"] (folder-filter "sample/simple" ".txt"))))