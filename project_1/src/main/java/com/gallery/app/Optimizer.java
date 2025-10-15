package com.gallery.app;

import com.gallery.tree.PhotoTree;
import com.gallery.tree.metadata.PhotoMetadata;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Optimizer {
    private static PhotoTree photoTree;
    private static String lastLoadedFolder = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите путь к папке с фотографиями: ");
        String folderPath = scanner.nextLine();
        lastLoadedFolder = folderPath;

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Путь указан неверно.");
            return;
        }

        photoTree = new PhotoTree("Галерея");

        loadPhotos(folder);

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\nТекущее дерево фотографий:");
                    photoTree.printTree();
                    break;
                case "2":
                    photoTree.organizeByDate();
                    System.out.println("фотографии организованы по дате");
                    break;
                case "3":
                    System.out.println("\nНайденные дубликаты: ");
                    Map<String, List<PhotoMetadata>> duplicates = photoTree.findDuplicates();
                    if (duplicates.isEmpty()) {
                        System.out.println("Дубликатов нет");
                    } else {
                        duplicates.forEach((hash, photos) -> {
                            System.out.println("Дубликаты для: " + hash);
                            photos.forEach(photo -> System.out.println("  - " + photo));
                        });
                    }
                    break;
                case "4":
                    System.out.print("Введите часть имени для поиска: ");
                    String searchName = scanner.nextLine();
                    List<PhotoMetadata> foundPhotos = photoTree.findPhotosByName(searchName);
                    if (foundPhotos.isEmpty()) {
                        System.out.println("Фотографии не найдены.");
                    } else {
                        System.out.println("Найденные фотографии:");
                        foundPhotos.forEach(photo -> System.out.println(photo));
                    }
                    break;
                case "5":
                    photoTree.deleteDuplicates();
                    reloadTree();
                    break;
                case "0":
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void loadPhotos(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && PhotoMetadata.isImageFile(file)) {
                PhotoMetadata metadata = new PhotoMetadata(file);
                photoTree.addPhoto("Все Фото", metadata);
            }
        }
        System.out.println("Загружено " + photoTree.getCountPhoto() + " фотографий.");
    }

    private static void reloadTree() {
        photoTree = new PhotoTree("Фото");
        loadPhotos(new File(lastLoadedFolder));
        photoTree.organizeByDate();
    }

    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1 - Показать структуру галереи");
        System.out.println("2 - Организовать фотографии по дате");
        System.out.println("3 - Найти дубликаты");
        System.out.println("4 - Поиск фотографий по имени");
        System.out.println("5 - Удалить дубликаты");
        System.out.println("0 - Выход");
        System.out.print("Выберите действие: ");
    }
}
