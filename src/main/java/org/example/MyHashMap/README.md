# 简易HashMap

实现了`get`,`set`,`remove`,`resize`方法

主要点在于`resize`，可以通过调整`loadFactor`来实现复杂度最小化，防止单链表化