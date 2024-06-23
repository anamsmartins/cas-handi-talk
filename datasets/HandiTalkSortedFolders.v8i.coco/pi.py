import os
import json
import shutil
from collections import defaultdict
from tqdm import tqdm

def create_class_folders(output_dir, class_names):
    for class_name in class_names:
        class_dir = os.path.join(output_dir, class_name)
        os.makedirs(class_dir, exist_ok=True)

def get_image_filename(image_id, images):
    for image in images:
        if image['id'] == image_id:
            return image['file_name']
    return None

def copy_images_to_class_folders(coco_annotation_file, image_dir, output_dir):
    with open(coco_annotation_file, 'r') as f:
        coco_data = json.load(f)
    
    categories = coco_data['categories']
    images = coco_data['images']
    annotations = coco_data['annotations']

    class_id_to_name = {category['id']: category['name'] for category in categories}
    class_names = [category['name'] for category in categories]

    create_class_folders(output_dir, class_names)

    image_id_to_annotations = defaultdict(list)
    for annotation in annotations:
        image_id_to_annotations[annotation['image_id']].append(annotation)

    for image_id, annotations in tqdm(image_id_to_annotations.items()):
        image_filename = get_image_filename(image_id, images)
        if not image_filename:
            continue
        
        src_image_path = os.path.join(image_dir, image_filename)
        for annotation in annotations:
            class_name = class_id_to_name[annotation['category_id']]
            dest_image_path = os.path.join(output_dir, class_name, image_filename)
            shutil.copy(src_image_path, dest_image_path)

if __name__ == '__main__':
    #basicamente limpa as cenas depois de organizar para funcionar com o coco sem manunteção, SE TAS COM MEDO DE PERDER DADOS DESATIVA ISTO!!!!!!
    justDoIt = True
    targets = ['test', 'train', 'valid']
    for target in targets:
        coco_annotation_file = f'{target}/_annotations.coco.json'
        image_dir = f'{target}'
        #create a folder named images in the data folder and put the images in it
        os.makedirs(f'{target}/images', exist_ok=True)
        output_dir = f'{target}/images'
        copy_images_to_class_folders(coco_annotation_file, image_dir, output_dir)
    if justDoIt:
      #go to target and remove any file with extensionion jpg
      for target in targets:
        #delete files with .jpg extension that arent in images
        for file in os.listdir(f'{target}'):
            if file.endswith(".jpg"):
                os.remove(f'{target}/{file}')
        #Move folders out of images
        for folder in os.listdir(f'{target}/images'):
            shutil.move(f'{target}/images/{folder}', f'{target}/{folder}')      
        #delete images folder
        shutil.rmtree(f'{target}/images')