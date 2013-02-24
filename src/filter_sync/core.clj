(ns filter-sync.core
  (:gen-class))

(import 
	java.io.FileInputStream
	'(java.util.zip ZipEntry ZipInputStream))

(defn zip-seq
	"Convert zip file into sequence."
	[filename]
	(lazy-seq
		(loop [zin (-> filename FileInputStream. ZipInputStream.) coll '()]
			(let [entry (.getNextEntry zin)]
				(if (nil? entry)
					coll
					(recur zin (conj coll (.getName entry))))))))

(defn zip-filter
	"Scan the zip file and keep only matched entries."
	[filename s]
	(lazy-seq
		(filter #(.endsWith % s) (zip-seq filename))))

(import
	org.apache.commons.io.IOUtils
	java.io.FileOutputStream
	'(java.util.zip ZipFile ZipOutputStream))

(defn zip-copy
	"Copy the zip with given entries."
	[from to entries]
	(with-open [zin (ZipFile. from)
		zout (-> to FileOutputStream. ZipOutputStream.)]
		(dorun
			(for [entry entries
				:let [zipEntry (.getEntry zin entry)]
				:when zipEntry]
				(do
					(println (str "Copying " entry "..."))
					(.putNextEntry zout zipEntry)
					(IOUtils/copy (.getInputStream zin zipEntry) zout))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (zip-copy "sample/simple/doc.zip" "sample/simple/out/doc.zip" '("a.txt" "b.gif")))
