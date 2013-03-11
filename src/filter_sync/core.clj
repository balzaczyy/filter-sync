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
	[filename conds]
	(lazy-seq
		(filter (fn [entry] (some #(.endsWith entry %) conds)) (zip-seq filename))))

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

(defn zip-filter-copy
	"Filter the zip file with specified extensions."
	[from to ext]
	(zip-copy from to (zip-filter from ext)))

(defn rel-path
	"Calculate relative path."
	[source target]
	(let [len (-> source .getPath .length)
		s (.getPath target)]
		(.substring s len)))

(import java.io.File)
(defn folder-copy
	"Copy the folder with given entries."
	[from to entries f]
	(do
		(.mkdirs to)
		(dorun
			(for [file entries
				:let [rel (rel-path from file)
				target (File. to rel)]]
				(if (.isDirectory file)
					(do
						(println (str "Creating " target "..."))
						(.mkdirs target))
					(do ;is file
						(when-not (.exists target)
							(let [parent (.getParentFile target)]
								(println (str "Creating " parent "..."))
								(.mkdirs parent)))
						(println (str "Copying to " target "..."))
						(f file target)))))))

(defn folder-filter
	"Scan the target folder and keep the matching entries with given extensions."
	[folder conds]
	(lazy-seq
		(filter (fn [file] (some (fn [s] (.endsWith (.getPath file) s)) conds)) (file-seq (File. folder)))))

(defn folder-filter-copy
	"Filter the folder with specified extensions."
	[from to ext]
	(folder-copy (File. from) (File. to) (folder-filter from (conj ext ".zip" ".jar"))
		(fn [source target]
			(let [filename (.getPath source)]
				(if (or (.endsWith filename ".zip") (.endsWith filename ".jar"))
					(zip-filter-copy source target ext)
					(clojure.java.io/copy source target))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (folder-filter-copy "sample/simple" "target/simple2" [".txt"])
)
