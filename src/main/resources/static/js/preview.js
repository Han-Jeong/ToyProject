function previewImage() {
    var preview = document.querySelector('#preview');
    var url = document.querySelector('#add_form_imageURL').value;
    //url존재할때만 display보여줌
    if(url){
        preview.style.display='block';
        preview.src = url;
    } else{
        alert('이미지가 존재하지 않습니다.');
    }
}