export default class StompClient {
  constructor() {
    this._socket = null;
    this._client = null;
    this._sub = null;
    this._pub = null;
  }

  init() {
    window.addEventListener('beforeunload', async () => {
      await this.disconnect();
    });
  }

  setSub(sub) {
    this._sub = sub;
  }

  setPub(pub) {
    this._pub = pub;
  }

  setTopic(sub, pub) {
    this._sub = sub;
    this._pub = pub;
  }

  send(message) {
    this._client.send(this._pub, {}, JSON.stringify(message));
  }

  connect() {
    return new Promise((resolve, reject) => {
      this._client.connect({}, (frame) => {
        this.handleConnect(frame);
        resolve();
      }, (error) => {
        this.handleError(error);
        reject(error);
      });
    });
  }

  disconnect() {
    return new Promise((resolve) => {
      this._client.disconnect(() => {
        this.handleDisconnect();
        resolve();
      });
    });
  }

  handleError(error) {
    console.log("Error >> " + error);
  }

  handleDisconnect() {
    console.log("Disconnected");
  }

  handleConnect(frame) {
    console.log("Connected >> " + frame);
    this._client.subscribe(this._sub, (message) => {
      const body = JSON.parse(message.body);
      console.log("Received >> " + body);
    });
  }
}
